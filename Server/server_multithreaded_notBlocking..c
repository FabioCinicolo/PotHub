#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <sys/poll.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <errno.h>
#include <arpa/inet.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <pthread.h>
#include "cJSON.h"
#include "cJSON.c"
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <netinet/in.h>
#include <mysql/mysql.h>
#include <math.h>

#define SERVER_PORT 12345
#define BACKLOG 100

#define THREADS_NUM 4 // Setting thread num to max core number because threads will never go in wait queue in this program(sockets are set to non blocking) so adding more threads wouldn't make difference
#define MAX_TASK_NUM 1000
#define MAX_MYSQL_CONNECTIONS 30

#define TRUE 1
#define FALSE 0

#define REPORT_POTHOLE 1
#define GET_POTHOLES_BY_RANGE 3
#define GET_USER_POTHOLES_BY_DAYS 2

#define FD_STOP_POLLING -2

typedef struct Pothole
{
    double latitude;
    double longitude;
    char *address;
    char *user;
    char *timestamp;
    int intensity;
} Pothole;

typedef struct Task
{
    int (*work)(void *);
    void *args;
} Task;

//-fd: value of the current file descriptor
//-old_fd: pointer storing the address of the ith file descriptor of the pollfd array
//-fds: reference to the pollfd array
typedef struct PollInfo
{
    int *old_fd;
    int fd;
    struct pollfd *fds;
} PollInfo;

int doWork(void *args);

Task task_queue[MAX_TASK_NUM];
MYSQL mysql_connections[MAX_MYSQL_CONNECTIONS];

int task_count = 0;
pthread_mutex_t thread_pool_mutex;
pthread_cond_t thread_pool_cond_empty;

pthread_mutex_t global_vars_mutex;

int compress_array = FALSE;
int num_threads_executing = 0;

void initializeThreadPool(pthread_t *threads, int thread_num);
void joinThreads(pthread_t *threads, int thread_num);
void addTask(Task task);
int executeTask(Task *task);
int reportPothole(Pothole pothole);
char *getPotholesByRangeJson(double latitude, double longitude, double range);
char *getUserPotholesByDays(char *username, int days);
double haversineDistance(double latitude1, double longitude1, double latitude2, double longitude2);
double toRad(double x);

static char *opt_host_name = "database-1.cgk0rzhm3j8s.eu-south-1.rds.amazonaws.com"; /* host (default=localhost) */
static char *opt_user_name = "fab_umb";                                              /* username (default=login name)*/
static char *opt_password = "Ciao161998_";                                           /* password (default=none) */
static unsigned int opt_port_num = 0;                                                /* port number (use built-in) */
static char *opt_socket_name = NULL;                                                 /* socket name (use built-in) */
static char *opt_db_name = "PothubDatabase";                                         /* database name (default=none) */
static unsigned int opt_flags = 0;                                                   /* connection flags (none) */

int main(int argc, char *argv[])
{
    int polls = 0;
    int on = 1;
    int poll_err, read_bytes;
    int listen_sd = -1, new_sd = -1;
    int desc_ready, end_server = FALSE;
    int close_conn;
    char buffer[80];
    struct sockaddr_in server_addr, client_addr;
    socklen_t client_addr_length;
    int timeout;
    int current_size = 0;
    pthread_t threads[THREADS_NUM];
    struct pollfd fds[200];
    int nfds = 1;
    int flag1;
    socklen_t len = sizeof(flag1);

    printf("Creating listening socket..\n");
    if ((listen_sd = socket(PF_INET, SOCK_STREAM | SOCK_NONBLOCK, IPPROTO_TCP)) < 0)
        perror("socket"), exit(EXIT_FAILURE);

    server_addr.sin_family = PF_INET;
    server_addr.sin_port = htons(SERVER_PORT);
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    memset(server_addr.sin_zero, 0, 8);

    if (bind(listen_sd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0)
    {
        perror("bind");
        close(listen_sd);
        exit(EXIT_FAILURE);
    }

    if (listen(listen_sd, BACKLOG) < 0)
    {
        perror("listen");
        close(listen_sd);
        exit(EXIT_FAILURE);
    }

    printf("LISTENING SOCKET CREATED\n\n");

    flag1 = 1800;
    printf("Setting TCP_KEEPIDLE TO %d seconds\n\n", flag1);
    if (setsockopt(listen_sd, SOL_TCP, TCP_KEEPIDLE, (void *)&flag1, len))
    {
        perror("ERROR: setsocketopt(), SO_KEEPIDLE");
        close(listen_sd), exit(EXIT_FAILURE);
    }

    flag1 = 5;
    printf("Setting TCP_KEEPCNT TO %d seconds\n\n", flag1);
    if (setsockopt(listen_sd, SOL_TCP, TCP_KEEPCNT, (void *)&flag1, len))
    {
        perror("ERROR: setsocketopt(), SO_KEEPCNT");
        close(listen_sd), exit(EXIT_FAILURE);
    }
    flag1 = 30;
    printf("Setting TCP_KEEPINTVL TO %d seconds\n\n", flag1);
    if (setsockopt(listen_sd, SOL_TCP, TCP_KEEPINTVL, (void *)&flag1, len))
    {
        perror("ERROR: setsocketopt(), SO_KEEPINTVL");
        close(listen_sd), exit(EXIT_FAILURE);
    }

    memset(fds, 0, sizeof(fds));

    // Set up the listening socket in the pollfd array, events = POLLIN on listening socket means that we are only interested in receiving new connections from client peers
    fds[0].fd = listen_sd;
    fds[0].events = POLLIN;

    // Set up timeout to 10 minutes, after that, poll returns even if no file descriptor is ready
    timeout = (0.1 * 60 * 1000);

    // Inizializing Mutex, Condition Variable and thread pool
    printf("INITIALIZING THREAD POOL AND GLOBAL VARIABLES...\n\n");
    pthread_mutex_init(&thread_pool_mutex, NULL);
    pthread_cond_init(&thread_pool_cond_empty, NULL);
    pthread_mutex_init(&global_vars_mutex, NULL);
    initializeThreadPool(threads, THREADS_NUM);

    printf("***SERVER SUCCESSFULLY STARTED***\n\n");

    printf("***SERVER SUCCESSFULLY STARTED***\n\n-----------------------------------------------------------------------------------------------------------\n\n");

    usleep(10000);
    // Loop waiting for incoming connections or for incoming data on any of the connected sockets
    do
    {
        printf("\n[-CLIENTS CONNECTED: %d\n-SOCKET DESCRIPTOR ARRAY:", nfds - 1);
        for (int i = 0; i < nfds; i++)
        {
            printf("    fd_%d = %d", i, fds[i].fd);
        }
        printf("]\n\n***n.%d POLL EXECUTED***\n\n", ++polls);
        if ((poll_err = poll(fds, nfds, timeout)) < 0)
        {
            perror("poll");
            break;
        }
        else if (poll_err == 0) // If poll timed out we check whether or not array needs to be compressed to remove unused socket descriptors
        {

            if (compress_array == TRUE && num_threads_executing == 0)
            {
                compress_array = FALSE;
                for (int i = 0; i < nfds; i++)
                {
                    if (fds[i].fd == -1)
                    {
                        for (int j = i; j < nfds - 1; j++)
                        {
                            fds[j].fd = fds[j + 1].fd;
                        }
                        i--;
                        nfds--;
                    }
                }
            }
            continue; // Jumps back to poll()
        }

        // One or more file descriptors are ready
        current_size = nfds;
        for (int i = 0; i < current_size; i++)
        {
            // If no events occurred for a file descriptor then we can directly jump to the next for-cycle iteration
            if (fds[i].revents == 0)
                continue;

            // We are only interested in accepting connections(listen socket) and reading from connected sockets, so if revents doesn't get filled by the Kernel with POLLIN, then something unexpected happened
            if (fds[i].revents != POLLIN)
            {
                if (fds[i].revents & POLLHUP) // IF POLLHUP is returned in revents it means that a client has been absent for too long (maybe he has lost connection); server can then proceed to release resources appropriately
                {
                    printf("SHUTTING DOWN CONNECTION WITH ABSENT PEER WITH FD: %d\n\n", fds[i].fd);
                    close(fds[i].fd);
                    fds[i].fd = -1;
                    // Remove unused file descriptor
                    for (int i = 0; i < nfds; i++)
                    {
                        if (fds[i].fd == -1)
                        {
                            for (int j = i; j < nfds - 1; j++)
                            {
                                fds[j].fd = fds[j + 1].fd;
                            }
                            i--;
                            nfds--;
                        }
                    }
                    break;
                }
                end_server = TRUE;
                break;
            }
            if (fds[i].fd == listen_sd)
            {
                // Listening socket is ready, proceed to accept all incoming connections that are queued up
                do
                {
                    // Accept each incoming connection. If EWOULDBLOCK is returned, then every connection has been accepted. Any other error code will cause the server to shut itself down
                    client_addr_length = sizeof(client_addr);
                    new_sd = accept(listen_sd, (struct sockaddr *)&client_addr, &client_addr_length);
                    if (new_sd < 0)
                    {
                        if (errno != EWOULDBLOCK)
                        {
                            perror("accept");
                            end_server = TRUE;
                        }
                        break;
                    }
                    printf("NEW CONNECTION ACCEPTED IP: %s\n\n", inet_ntoa(client_addr.sin_addr));

                    // All accepted sockets will check for absent and potentially dead client peers By sending periodically heartbeats (packets with no data)
                    flag1 = 1;
                    if (setsockopt(new_sd, SOL_SOCKET, SO_KEEPALIVE, (void *)&flag1, len) < 0)
                    {
                        perror("setsockopt()");
                        close(new_sd);
                    }
                    // Put the socket in non-blocking mode
                    if (fcntl(new_sd, F_SETFL, fcntl(new_sd, F_GETFL) | O_NONBLOCK) < 0)
                        perror("fcntl"), exit(EXIT_FAILURE);

                    printf("ACCEPTED SOCKET SET TO NON-BLOCKING AND KEEPALIVE ON\n\n");
                    // Add the file descriptor of the new socket in the pollfd array
                    fds[nfds].fd = new_sd;
                    fds[nfds].events = POLLIN;
                    nfds++;

                } while (1);
            }

            // Accepted socket is readable
            else
            {
                // At this point we can pass the task to a new thread
                PollInfo info = {
                    .old_fd = &(fds[i].fd),
                    .fd = fds[i].fd,
                    .fds = fds};
                Task read_task = {
                    .args = (void *)&info,
                    .work = &doWork};
                addTask(read_task);
                // This temporary disables file descriptor, to avoid poll to return immediately even if a thread is already taking care of the task. Thread will reset the value as soon as everything has been read so that poll can listen to its events again
                fds[i].fd = FD_STOP_POLLING;
            }
        }

    } while (!end_server);

    // Closing all opened connections before shutting down server
    for (int i = 0; i < nfds; i++)
    {
        printf("CLOSING CONNECTION ASSOCIATED TO FD: %d\n\n", fds[i].fd);
        if (fds[i].fd >= 0)
            if (close(fds[i].fd) < 0)
                perror("close");
    }

    joinThreads(threads, THREADS_NUM);

    pthread_mutex_destroy(&global_vars_mutex);
    pthread_mutex_destroy(&thread_pool_mutex);
    pthread_cond_destroy(&thread_pool_cond_empty);

    return 0;
}

void *threadStarter(void *args)
{
    printf("THREAD %ld RUNNING\n\n", pthread_self());
    while (1)
    {
        Task task;
        pthread_mutex_lock(&thread_pool_mutex);
        while (task_count == 0)
        {
            pthread_cond_wait(&thread_pool_cond_empty, &thread_pool_mutex);
        }
        task = task_queue[0];
        for (int i = 0; i < task_count - 1; i++)
            task_queue[i] = task_queue[i + 1];
        task_count--;

        pthread_mutex_unlock(&thread_pool_mutex);
        printf("THREAD %ld EXECUTING TASK\n\n", pthread_self());
        int ret = executeTask(&task);
    }
}

void initializeThreadPool(pthread_t *threads, int thread_num)
{

    for (int i = 0; i < thread_num; i++)
        if (pthread_create(&threads[i], NULL, &threadStarter, NULL) < 0)
            perror("pthread_create"), exit(EXIT_FAILURE);
}

void joinThreads(pthread_t *threads, int thread_num)
{

    for (int i = 0; i < thread_num; i++)
        if (pthread_join(threads[i], NULL) < 0)
            perror("pthread_join"), exit(EXIT_FAILURE);
}

void addTask(Task task)
{
    pthread_mutex_lock(&thread_pool_mutex);
    task_queue[task_count++] = task;
    pthread_mutex_unlock(&thread_pool_mutex);
    pthread_cond_signal(&thread_pool_cond_empty);
}

int executeTask(Task *task)
{
    pthread_mutex_lock(&global_vars_mutex);
    num_threads_executing++;
    pthread_mutex_unlock(&global_vars_mutex);
    int ret = task->work(task->args);
    pthread_mutex_lock(&global_vars_mutex);
    num_threads_executing--;
    pthread_mutex_unlock(&global_vars_mutex);
    return ret;
}

int doWork(void *args)
{

    int close_conn = FALSE;
    ssize_t read_bytes, sent_bytes = 0, buff_len, total_bytes_sent = 0;
    int *old_fd = ((PollInfo *)args)->old_fd;
    int fd = ((PollInfo *)args)->fd;
    struct pollfd *fds = ((PollInfo *)args)->fds;
    char buffer[1024];
    char *json_message;
    cJSON *action, *json;

    do
    {
        printf("ACCEPTED SOCKET WITH FD %d IS READY TO BE READ ON\n\n", fd);
        // Read data from accepted socket, if EWOULDBLOCK is returned it means that there are no more bytes to read. Any other error code will cause the server to close the connection
        read_bytes = recv(fd, buffer, sizeof(buffer), 0);
        if (read_bytes < 0)
        {
            if (errno != EWOULDBLOCK)
            {
                perror("recv");
                close_conn = TRUE;
                goto end;
            }

            break; // If errno = EWOULDBLOCK THEN I CAN PROCEED TO PARSE JSON AND GET THE ACTION
        }

        // If connection has been closed by the client then set close_conn flag to true
        if (read_bytes == 0)
        {
            printf("ciao1\n");
            close_conn = TRUE;
            goto end;
        }

    } while (TRUE);

    // PARSING LOGIC
    printf("%s\n", buffer);
    json = cJSON_Parse(buffer);
    if (!json)
    {
        const char *error_ptr = cJSON_GetErrorPtr();
        fprintf(stderr, "COULD NOT PARSE JSON\n\n");
        goto end;
    }
    else
    {
        action = cJSON_GetObjectItemCaseSensitive(json, "action");
        switch (action->valueint)
        {
        case REPORT_POTHOLE:
        {
            Pothole pothole = {
                .latitude = cJSON_GetObjectItemCaseSensitive(json, "latitude")->valuedouble,
                .longitude = cJSON_GetObjectItemCaseSensitive(json, "longitude")->valuedouble,
                .address = cJSON_GetObjectItemCaseSensitive(json, "address")->valuestring,
                .user = cJSON_GetObjectItemCaseSensitive(json, "user")->valuestring,
                .timestamp = cJSON_GetObjectItemCaseSensitive(json, "timestamp")->valuestring,
                .intensity = cJSON_GetObjectItemCaseSensitive(json, "intensity")->valueint};
            reportPothole(pothole);
            break;
        }
        case GET_POTHOLES_BY_RANGE:
        {
                close_conn=TRUE;
            printf("GETTING POTHOLES BY RANGE...\n\n");
            json_message = getPotholesByRangeJson(cJSON_GetObjectItemCaseSensitive(json, "latitude")->valuedouble, cJSON_GetObjectItemCaseSensitive(json, "longitude")->valuedouble, cJSON_GetObjectItemCaseSensitive(json, "range")->valuedouble);
            if (!json_message)
            {
                fprintf(stderr, "COULD NOT GET JSON\n\n");
                goto end;
            }
            buff_len = strlen(json_message);
            json_message = realloc(json_message, buff_len + 1);
            json_message[buff_len] = '\n';
            do
            {
                  sent_bytes = send(fd, json_message, buff_len + 1,0);
                if (sent_bytes < 0)
                {
                    if (errno != EWOULDBLOCK)
                    {
                        perror("send");
                        break;
                    }
                }else
                        total_bytes_sent += sent_bytes;
            } while (total_bytes_sent <= buff_len); // It can happen that message size is greather than socket buffer, socket will then return with errno = EWOULDBLOCK
            break;
        }
        case GET_USER_POTHOLES_BY_DAYS:
        {
                close_conn=TRUE;
            printf("GETTING USER POTHOLES BY %d DAYS...\n\n", cJSON_GetObjectItemCaseSensitive(json, "days")->valueint);
            json_message = getUserPotholesByDays(cJSON_GetObjectItemCaseSensitive(json, "user")->valuestring, cJSON_GetObjectItemCaseSensitive(json, "days")->valueint);
            printf("%s\n", json_message);
            if (!json_message)
            {
                fprintf(stderr, "COULD NOT GET JSON\n\n");
                goto end;
            }
            buff_len = strlen(json_message);
            json_message = realloc(json_message, buff_len + 1);
            json_message[buff_len] = '\n';
            do
            {  sent_bytes = send(fd, json_message, buff_len + 1,0);
                if (sent_bytes < 0)
                {
                    if (errno != EWOULDBLOCK)
                    {
                        perror("send");
                        break;
                    }
                }else
                        total_bytes_sent += sent_bytes;

            } while (total_bytes_sent <= buff_len); // It can happen that message size is greather than socket buffer, socket will then return with errno = EWOULDBLOCK
            break;
        }
        default:
            fprintf(stderr, "WRONG ACTION\n\n");
        }
    }
    free(json_message);
    cJSON_Delete(json);
end:
    // If close_conn was set, we set the compress_array flag to true
    if (close_conn)
    {
        printf("CLOSING CONNECTION ASSOCIATED WITH FD: %d\n\n", fd);
        close(fd);
        *old_fd = -1;
        compress_array = TRUE;
    }
    else
        *old_fd = fd;

    return 0;
}

int reportPothole(Pothole pothole)
{
    MYSQL_STMT *statement_insert_pothole;
    MYSQL_BIND bind_insert_pothole[6];
    MYSQL *mysql;

    mysql = mysql_init(NULL);
    if (mysql == NULL)
    {
        fprintf(stderr, "mysql_init() failed\n\n"), exit(EXIT_FAILURE);
    }
    if (!mysql_real_connect(mysql, opt_host_name, opt_user_name, opt_password, opt_db_name, opt_port_num, opt_socket_name, opt_flags))
    {
        fprintf(stderr, "mysql_real_connect() failed\n\n %s", mysql_error(mysql)), exit(EXIT_FAILURE);
        mysql_close(mysql);
    }

    // Initializing insertPotholeStatement
    statement_insert_pothole = mysql_stmt_init(mysql);

    if (statement_insert_pothole != NULL)
        if (mysql_stmt_errno(statement_insert_pothole) != 0)
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_insert_pothole)), exit(EXIT_FAILURE);

    ////Preparing insertPotholeStatement
    mysql_stmt_prepare(statement_insert_pothole, "INSERT INTO PotholeReport (Latitude,Longitude,Address,User,Timestamp,Intensity) VALUES(?,?,?,?,?,?)", 100);

    if (statement_insert_pothole != NULL)
        if (mysql_stmt_errno(statement_insert_pothole) != 0)
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_insert_pothole)), exit(EXIT_FAILURE);

    memset(bind_insert_pothole, 0, sizeof(bind_insert_pothole));

    // Setting latitude parameter
    bind_insert_pothole[0].buffer_type = MYSQL_TYPE_DOUBLE;
    bind_insert_pothole[0].buffer = &(pothole.latitude);
    bind_insert_pothole[0].buffer_length = sizeof(double);

    // Setting longitude parameter
    bind_insert_pothole[1].buffer_type = MYSQL_TYPE_DOUBLE;
    bind_insert_pothole[1].buffer = &(pothole.longitude);
    bind_insert_pothole[1].buffer_length = sizeof(double);

    // Setting Address parameter
    bind_insert_pothole[2].buffer_type = MYSQL_TYPE_VARCHAR;
    bind_insert_pothole[2].buffer = pothole.address;
    bind_insert_pothole[2].buffer_length = sizeof(pothole.address);
    size_t address_length = strlen(pothole.address);
    bind_insert_pothole[2].length = &address_length;

    // Setting User parameter
    bind_insert_pothole[3].buffer_type = MYSQL_TYPE_VARCHAR;
    bind_insert_pothole[3].buffer = pothole.user;
    bind_insert_pothole[3].buffer_length = sizeof(pothole.user);
    size_t user_length = strlen(pothole.user);
    bind_insert_pothole[3].length = &user_length;

    // Setting Timestamp parameter
    bind_insert_pothole[4].buffer_type = MYSQL_TYPE_VARCHAR;
    bind_insert_pothole[4].buffer = pothole.timestamp;
    bind_insert_pothole[4].buffer_length = sizeof(pothole.timestamp);
    size_t timestamp_length = strlen(pothole.timestamp);
    bind_insert_pothole[4].length = &timestamp_length;

    // Setting Intensity parameter
    bind_insert_pothole[5].buffer_type = MYSQL_TYPE_LONG;
    bind_insert_pothole[5].buffer = &(pothole.intensity);
    bind_insert_pothole[5].buffer_length = sizeof(pothole.intensity);

    // Binding parameters to insertPotholeStatement
    mysql_stmt_bind_param(statement_insert_pothole, bind_insert_pothole);
    if (statement_insert_pothole != NULL)
        if (mysql_stmt_errno(statement_insert_pothole) != 0)
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_insert_pothole)), exit(EXIT_FAILURE);

    mysql_stmt_execute(statement_insert_pothole);
    if (statement_insert_pothole != NULL)
        if (mysql_stmt_errno(statement_insert_pothole) != 0)
            fprintf(stderr, "mysql_stmt_execute() error: %s\n\n", mysql_stmt_error(statement_insert_pothole));

    mysql_close(mysql);
    return mysql_stmt_affected_rows(statement_insert_pothole) == 1 ? EXIT_SUCCESS : EXIT_FAILURE;
}

char *getPotholesByRangeJson(double latitude, double longitude, double range)
{
    MYSQL *mysql = mysql_init(NULL);
    MYSQL_RES *result;
    MYSQL_ROW row;
    double distance;
    cJSON *json;
    cJSON *potholes;
    cJSON *pothole;
    cJSON *lat;
    cJSON *lon;
    cJSON *address;
    cJSON *user;
    cJSON *timestamp;
    cJSON *intensity;

    char *json_string;

    if (mysql == NULL)
    {
        fprintf(stderr, "mysql_init() failed\n\n");
        return NULL;
    }
    if (!mysql_real_connect(mysql, opt_host_name, opt_user_name, opt_password, opt_db_name, opt_port_num, opt_socket_name, opt_flags))
    {
        fprintf(stderr, "mysql_real_connect() failed\n\n %s", mysql_error(mysql));
        mysql_close(mysql);
        return NULL;
    }
    mysql_query(mysql, "SELECT  Latitude, Longitude, Address, User, Timestamp, Intensity FROM PotholeReport");
    if (!(result = mysql_store_result(mysql)))
    {
        fprintf(stderr, "%s\n", mysql_error(mysql));
        return NULL;
    }

    json = cJSON_CreateObject();
    potholes = cJSON_CreateArray();

    if (!json || !potholes)
        goto end;
    cJSON_AddItemToObject(json, "Potholes", potholes);

    while ((row = mysql_fetch_row(result)))
    {
        // Computing distance between DB pothole and client latitude and longitude
        distance = haversineDistance(atof(row[0]), atof(row[1]), latitude, longitude);
        if (distance <= range)
        {
            pothole = cJSON_CreateObject();
            cJSON_AddItemToArray(potholes, pothole);
            lat = cJSON_CreateNumber(atof(row[0]));
            lon = cJSON_CreateNumber(atof(row[1]));
            address = cJSON_CreateString(row[2]);
            user = cJSON_CreateString(row[3]);
            timestamp = cJSON_CreateString(row[4]);
            intensity = cJSON_CreateNumber(atoi(row[5]));
            if (!pothole || !lat || !lon || !address || !user || !timestamp || !intensity)
                goto end;
            cJSON_AddItemToObject(pothole, "latitude", lat);
            cJSON_AddItemToObject(pothole, "longitude", lon);
            cJSON_AddItemToObject(pothole, "address", address);
            cJSON_AddItemToObject(pothole, "user", user);
            cJSON_AddItemToObject(pothole, "timestamp", timestamp);
            cJSON_AddItemToObject(pothole, "intensity", intensity);
        }
    }
    json_string = cJSON_PrintUnformatted(json);
    cJSON_Delete(json);
end:
    mysql_free_result(result);
    mysql_close(mysql);
    return json_string;
}

char *getUserPotholesByDays(char *username, int days)
{
    MYSQL *mysql = mysql_init(NULL);
    MYSQL_RES *result;
    MYSQL_ROW row;
    double distance;
    cJSON *json;
    cJSON *pothole;
    cJSON *lat;
    cJSON *lon;
    cJSON *address;
    cJSON *user;
    cJSON *timestamp;
    cJSON *intensity;

    char *json_string;

    if (mysql == NULL)
    {
        fprintf(stderr, "mysql_init() failed\n\n");
        return NULL;
    }
    if (!mysql_real_connect(mysql, opt_host_name, opt_user_name, opt_password, opt_db_name, opt_port_num, opt_socket_name, opt_flags))
    {
        fprintf(stderr, "mysql_real_connect() failed\n\n %s", mysql_error(mysql));
        mysql_close(mysql);
        return NULL;
    }
    mysql_query(mysql, "SELECT  Latitude, Longitude, Address, User, Timestamp, Intensity FROM PotholeReport");
    if (!(result = mysql_store_result(mysql)))
    {
        fprintf(stderr, "%s\n", mysql_error(mysql));
        return NULL;
    }

    json = cJSON_CreateArray();

    if (!json)
        goto end;

    while ((row = mysql_fetch_row(result)))
    {
        pothole = cJSON_CreateObject();
        cJSON_AddItemToArray(json, pothole);
        lat = cJSON_CreateNumber(atof(row[0]));
        lon = cJSON_CreateNumber(atof(row[1]));
        address = cJSON_CreateString(row[2]);
        user = cJSON_CreateString(row[3]);
        timestamp = cJSON_CreateString(row[4]);
        intensity = cJSON_CreateNumber(atoi(row[5]));
        if (!pothole || !lat || !lon || !address || !user || !timestamp || !intensity)
            goto end;
        cJSON_AddItemToObject(pothole, "latitude", lat);
        cJSON_AddItemToObject(pothole, "longitude", lon);
        cJSON_AddItemToObject(pothole, "address", address);
        cJSON_AddItemToObject(pothole, "user", user);
        cJSON_AddItemToObject(pothole, "timestamp", timestamp);
        cJSON_AddItemToObject(pothole, "intensity", intensity);
    }
    json_string = cJSON_PrintUnformatted(json);
    cJSON_Delete(json);
end:
    mysql_free_result(result);
    mysql_close(mysql);
    return json_string;
}

double haversineDistance(double latitude1, double longitude1, double latitude2, double longitude2)
{
    double earth_radius = 6378137.0;
    double d_lat = toRad(latitude2 - latitude1);
    double d_long = toRad(longitude2 - longitude1);
    double a = sin(d_lat / 2) * sin(d_lat / 2) + cos(toRad(latitude1)) * cos(toRad(latitude2)) * sin(d_long / 2) * sin(d_long / 2);
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));
    return earth_radius * c;
}

double toRad(double x)
{
    return x * M_PI / 180;
}

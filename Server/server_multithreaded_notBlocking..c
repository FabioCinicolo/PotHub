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

#define SERVER_PORT 12345
#define BACKLOG 32

#define THREADS_NUM 4
#define MAX_TASK_NUM 20

#define TRUE 1
#define FALSE 0

#define REPORT_POTHOLE 1
#define GET_POTHOLES_BY_RANGE 2
#define GET_USER_POTHOLES_BY_DATE 3

#define FD_STOP_POLLING -2


typedef struct Pothole{
    double latitude;
    double longitude;
    char*address;
    char*user;
    char*timestamp;
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
Pothole* getPotholesByRange(double range);
Pothole* getUserPotholesByDate(char*username, char*timestamp);

int main(int argc, char *argv[])
{
    int polls = 0;
    int len, on = 1;
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

    // Creating listening socket, set to non block. Incoming connections will also be nonblocking as they inherit properties from listening socket.
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

    printf("***SERVER SUCCESSFULLY STARTED***\n");

    memset(fds, 0, sizeof(fds));

    // Set up the listening socket in the pollfd array, events = POLLIN means that we are only interested in receiving new connections with this socket
    fds[0].fd = listen_sd;
    fds[0].events = POLLIN;

    // Set up timeout to 3 minutes, after that, poll returns even if no file descriptor is ready
    timeout = (0.1 * 60 * 1000);

    // Inizializing Mutex, Condition Variable and thread pool
    pthread_mutex_init(&thread_pool_mutex, NULL);
    pthread_cond_init(&thread_pool_cond_empty, NULL);
    pthread_mutex_init(&global_vars_mutex, NULL);
    initializeThreadPool(threads, THREADS_NUM);

    usleep(10000);
    // Loop waiting for incoming connections or for incoming data on any of the connected sockets
    do
    {
        printf("\n[-nfds: %d\n-SOCKET DESCRIPTOR ARRAY:", nfds);
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
                fprintf(stderr, "***Unexpected event on file descriptor: %d***\n", fds[i].revents);
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

                    // Test if the socket is in non-blocking mode:
                    if (fcntl(new_sd, F_GETFL) & O_NONBLOCK)
                    {
                        // socket is non-blocking
                    }
                    else
                    {
                        // Put the socket in non-blocking mode
                        if (fcntl(new_sd, F_SETFL, fcntl(new_sd, F_GETFL) | O_NONBLOCK) < 0)
                        {
                        }
                    }

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
                // This avoids poll to return immediately and causing segmentation fault, thread will modify the value as soon as everything has been read so that poll can listen to its events again
                fds[i].fd = FD_STOP_POLLING;
            }
        }

    } while (!end_server);

    // Closing all opened connections before shutting down server
    for (int i = 0; i < nfds; i++)
    {
        printf("***Closing connection associated to SD: %d***\n", fds[i].fd);
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
    printf("***THREAD %ld RUNNING***\n", pthread_self());
    while (1)
    {
        Task task;
        pthread_mutex_lock(&thread_pool_mutex);
        while (task_count == 0)
        {
            pthread_cond_wait(&thread_pool_cond_empty, &thread_pool_mutex);
        }
        printf("Thread id: %ld\n", pthread_self());
        task = task_queue[0];
        for (int i = 0; i < task_count - 1; i++)
            task_queue[i] = task_queue[i + 1];
        task_count--;

        pthread_mutex_unlock(&thread_pool_mutex);
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
    ssize_t read_bytes;
    int *old_fd = ((PollInfo *)args)->old_fd;
    int fd = ((PollInfo *)args)->fd;
    struct pollfd *fds = ((PollInfo *)args)->fds;
    char buffer[1024];
    Pothole*potholes;

    do
    {
        printf("***Accepted socket is ready to be read on, SD: %d***\n", fd);
        // Read data from accepted socket, if EWOULDBLOCK is returned it means that there are no more bytes to read. Any other error code will cause the server to close the connection
        read_bytes = recv(fd, buffer, sizeof(buffer), 0);
        if (read_bytes < 0)
        {
            if (errno != EWOULDBLOCK)
            {
                perror("recv");
                close_conn = TRUE;
            }
            break;
        }

        // If connection has been closed by the client then set close_conn flag to true
        if (read_bytes == 0)
        {
            close_conn = TRUE;
            break;
        }

        buffer[read_bytes] = '\0';
        // Read buffer
        printf("Buffer content: %s\n", buffer);

    } while (TRUE);

    // If close_conn was set, we set the compress_array flag to true
    if (close_conn)
    {
        printf("***Closing connection associated to SD: %d***\n", fd);
        close(fd);
        *old_fd = -1;
        compress_array = TRUE;
    }
    else
        *old_fd = fd;

    //PARSING LOGIC
    cJSON *json = cJSON_Parse(buffer);
    if (!json)
    {
        const char *error_ptr = cJSON_GetErrorPtr();
        fprintf(stderr, "***Couldn't parse json%s\n", error_ptr != NULL ? "Error before %s", error_ptr : "");
    }
    else
    {
        cJSON * action = cJSON_GetObjectItemCaseSensitive(json, "action");
        switch(action->valueint)
            {
                case REPORT_POTHOLE:{
                    Pothole pothole = {
                        .latitude = cJSON_GetObjectItemCaseSensitive(json, "latitude")->valuedouble,
                        .longitude = cJSON_GetObjectItemCaseSensitive(json, "longitude")->valuedouble,
                        .address = cJSON_GetObjectItemCaseSensitive(json, "address")->valuestring,
                        .user = cJSON_GetObjectItemCaseSensitive(json, "user")->valuestring,
                        .timestamp = cJSON_GetObjectItemCaseSensitive(json, "timestamp")->valuestring,
                        .intensity = cJSON_GetObjectItemCaseSensitive(json, "intensity")->valueint
                    };
                    reportPothole(pothole);
                    break;
                }
                case GET_POTHOLES_BY_RANGE:{
                    potholes = getPotholesByRange(cJSON_GetObjectItemCaseSensitive(json, "range")->valuedouble);
                    //Insert here send potholes to client logic
                    break;
                }
                case GET_USER_POTHOLES_BY_DATE:{
                    getUserPotholesByDate(cJSON_GetObjectItemCaseSensitive(json, "user")->valuestring, cJSON_GetObjectItemCaseSensitive(json, "timestamp")->valuestring);
                    //Insert here send potholes to client logic
                    break;
                }
                default:
                    break;
            }
    }
    return 0;
}

int reportPothole(Pothole pothole){
    return 0;
}

Pothole* getPotholesByRange(double range){
    return NULL;
}

Pothole* getUserPotholesByDate(char*user, char*timestamp){
    return NULL;
}
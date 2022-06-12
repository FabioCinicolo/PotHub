#include <sys/socket.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include "cJSON/cJSON.h"
#include "cJSON/cJSON.c"
#include <mysql/mysql.h>
#include "Headers/logic.h"
#include "Headers/utilities.h"

int doWork(void *args)
{

    int close_conn = FALSE; // Close connection flag
    ssize_t read_bytes, sent_bytes = 0, buff_len, bytes_remaining = 0;
    int *old_fd = ((PollInfo *)args)->old_fd;     // We need this reference to restore the old value of the file descriptor
    int fd = ((PollInfo *)args)->fd;              // Current file descriptor
    struct pollfd *fds = ((PollInfo *)args)->fds; // Reference to the pollfd struct array
    char buffer[1024];                            // Buffer
    char *json_message;
    cJSON *action, *json;
    cJSON *latitude, *longitude, *range, *date, *user, *address, *timestamp, *intensity;

    printf("THREAD %ld DEALING WITH FD %d STARTED EXECUTING\n", pthread_self(), fd);
    do
    {
        // Read data from accepted socket, if EWOULDBLOCK is returned it means that there are no more bytes to read.
        read_bytes = recv(fd, buffer, sizeof(buffer), 0);
        if (read_bytes < 0)
        {
            if (errno != EWOULDBLOCK)
            {
                // Another thread already closed connection with fd, BAD FILE DESCRIPTOR IS SET AS errno,
                // WE THEN STOP THE COMPUTATION AND RELEASE RESOURCES, or another error occurred
                close_conn = TRUE;
                goto end;
            }

            break; // If errno = EWOULDBLOCK (Message length is fixed, will always be less than 1024 bytes) THEN I CAN PROCEED TO PARSE JSON AND GET THE ACTION
        }

        // If connection has been closed by the client then set close_conn flag to true
        if (read_bytes == 0)
        {
            close_conn = TRUE;
            goto end;
        }

    } while (TRUE);

    // PARSING LOGIC
    json = cJSON_Parse(buffer); // Parsing client message
    if (!json)                  // If message cannot be parsed as json
    {
        const char *error_ptr = cJSON_GetErrorPtr();
        fprintf(stderr, "COULD NOT PARSE JSON\n\n");
        close_conn = TRUE;
        goto end;
    }
    else
    { // Extracting action from json

        action = cJSON_GetObjectItemCaseSensitive(json, "action");
        if (!action)
        {
            close_conn = TRUE;
            goto end;
        }

        switch (action->valueint)
        {
        case REPORT_POTHOLE:
        {
            printf("REPORTING POTHOLE\n\n");
            // Those checks allow the server not to crash when the message is in the wrong format
            latitude = cJSON_GetObjectItemCaseSensitive(json, "latitude");
            longitude = cJSON_GetObjectItemCaseSensitive(json, "longitude");
            address = cJSON_GetObjectItemCaseSensitive(json, "address");
            user = cJSON_GetObjectItemCaseSensitive(json, "user");
            timestamp = cJSON_GetObjectItemCaseSensitive(json, "timestamp");
            intensity = cJSON_GetObjectItemCaseSensitive(json, "intensity");
            if (!latitude || !longitude || !address || !user || !timestamp || !intensity)
            {
                close_conn = TRUE;
                goto end;
            }

            Pothole pothole = {
                .latitude = latitude->valuedouble,
                .longitude = longitude->valuedouble,
                .address = address->valuestring,
                .user = user->valuestring,
                .timestamp = timestamp->valuestring,
                .intensity = intensity->valueint};
            reportPothole(pothole);
            break;
        }
        case GET_POTHOLES_BY_RANGE:
        {
            printf("GETTING POTHOLES BY RANGE...\n\n");

            latitude = cJSON_GetObjectItemCaseSensitive(json, "latitude");
            longitude = cJSON_GetObjectItemCaseSensitive(json, "longitude");
            range = cJSON_GetObjectItemCaseSensitive(json, "range");

            if (!latitude || !longitude || !range)
            {
                close_conn = TRUE;
                goto end;
            }

            json_message = getPotholesByRangeJson(latitude->valuedouble, longitude->valuedouble, range->valuedouble);
            if (!json_message)
            {
                fprintf(stderr, "COULD NOT GET JSON\n\n");

                close_conn = TRUE;
                goto end;
            }
            buff_len = strlen(json_message);
            json_message = realloc(json_message, buff_len + 1);
            json_message[buff_len] = '\n'; // Endline will be message termination character
            bytes_remaining = buff_len + 1;
            do
            {
                sent_bytes = send(fd, json_message, bytes_remaining, 0);
                if (sent_bytes < 0)
                {
                    if (errno != EWOULDBLOCK)
                    {
                        // Another thread already closed connection with fd, BAD FILE DESCRIPTOR IS SET AS errno, WE THEN STOP THE COMPUTATION AND RELEASE RESOURCES, or another error occurred
                        close_conn = TRUE;
                        goto end;
                    }
                    // Socket buffer is full, could not send any bytes
                }
                else // A portion of the buffer has been sent
                {
                    bytes_remaining -= sent_bytes;
                    json_message += sent_bytes;
                }
            } while (bytes_remaining > 0);
            break;
        }
        case GET_USER_POTHOLES_BY_DAYS:
        {
            user = cJSON_GetObjectItemCaseSensitive(json, "user");
            date = cJSON_GetObjectItemCaseSensitive(json, "date");
            printf("GETTING USER POTHOLES BY DAYS...\n\n");
            if (!user || !date)
            {
                close_conn = TRUE;
                goto end;
            }

            json_message = getUserPotholesBy14DaysJson(user->valuestring, date->valuestring);
            if (!json_message)
            {
                fprintf(stderr, "COULD NOT GET JSON\n\n");
                close_conn = TRUE;
                goto end;
            }
            buff_len = strlen(json_message);
            json_message = realloc(json_message, buff_len + 1);
            json_message[buff_len] = '\n'; // Endline will be message termination character
            bytes_remaining = buff_len + 1;
            do
            {
                sent_bytes = send(fd, json_message, bytes_remaining, 0);
                if (sent_bytes < 0)
                {
                    if (errno != EWOULDBLOCK)
                    {
                        // Another thread already closed connection with fd, BAD FILE DESCRIPTOR IS SET AS errno, WE THEN STOP THE COMPUTATION AND RELEASE RESOURCES, or another error occurred
                        close_conn = TRUE;
                        goto end;
                    }
                    // Socket buffer is full, could not send any bytes
                }
                else // A portion of the buffer has been sent
                {
                    bytes_remaining -= sent_bytes;
                    json_message += sent_bytes;
                }
            } while (bytes_remaining > 0);
            break;
        }
        default:
        {
            fprintf(stderr, "WRONG ACTION\n\n");
            close_conn = TRUE;
        }
        }
    }
end:
    if (!json_message)
        free(json_message);
    if (!json)
        cJSON_Delete(json);
    if (!action)
        cJSON_Delete(action);
    if (!latitude)
        cJSON_Delete(latitude);
    if (!longitude)
        cJSON_Delete(longitude);
    if (!range)
        cJSON_Delete(range);
    if (!date)
        cJSON_Delete(date);
    if (!user)
        cJSON_Delete(user);
    if (!address)
        cJSON_Delete(address);
    if (!timestamp)
        cJSON_Delete(timestamp);
    if (!intensity)
        cJSON_Delete(intensity);

    if (close_conn == TRUE)
    {
        printf("CLOSING CONNECTION ASSOCIATED WITH FD: %d\n\n", fd);
        close(fd);    // This will fail whenever another thread has already closed connection with this file descriptor
        *old_fd = -1; // Tells program to remove file descriptor from the cell of the poll fd structure
        compress_array = TRUE;
    }
    else
        *old_fd = fd; // Restoring old file descriptor value in the pollfd struct, so that poll, at the next iteration, can listen to its events again

    printf("THREAD %ld DEALING WITH FD %d TERMINATED\n", pthread_self(), fd);
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
        fprintf(stderr, "mysql_init() failed\n\n");
        return 0;
    }
    if (!mysql_real_connect(mysql, opt_host_name, opt_user_name, opt_password, opt_db_name, opt_port_num, opt_socket_name, opt_flags))
    {
        fprintf(stderr, "mysql_real_connect() failed\n\n %s", mysql_error(mysql));
        return 0;
        mysql_close(mysql);
    }

    // Initializing insertPotholeStatement
    statement_insert_pothole = mysql_stmt_init(mysql);

    if (statement_insert_pothole != NULL)
        if (mysql_stmt_errno(statement_insert_pothole) != 0)
        {
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_insert_pothole));
            return 0;
        }

    ////Preparing insertPotholeStatement
    mysql_stmt_prepare(statement_insert_pothole, "INSERT INTO PotholeReport (Latitude,Longitude,Address,User,Timestamp,Intensity) VALUES(?,?,?,?,?,?)", 100);

    if (statement_insert_pothole != NULL)
        if (mysql_stmt_errno(statement_insert_pothole) != 0)
        {
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_insert_pothole));
            return 0;
        }

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
        {
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_insert_pothole));
            return 0;
        }

    // Executing statement
    mysql_stmt_execute(statement_insert_pothole);
    if (statement_insert_pothole != NULL)
        if (mysql_stmt_errno(statement_insert_pothole) != 0)
            fprintf(stderr, "mysql_stmt_execute() error: %s\n\n", mysql_stmt_error(statement_insert_pothole));

    mysql_close(mysql);
    return mysql_stmt_affected_rows(statement_insert_pothole) == 1 ? 1 : 0;
}

char *getPotholesByRangeJson(double latitude, double longitude, double range)
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
        mysql_close(mysql);
        return NULL;
    }

    json = cJSON_CreateArray();

    if (!json)
        goto end;

    while ((row = mysql_fetch_row(result)))
    {
        // Computing distance between DB pothole and client latitude and longitude
        distance = haversineDistance(atof(row[0]), atof(row[1]), latitude, longitude);
        // We only take potholes which are in the range of a point specified by user
        if (distance <= range)
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
    }
    json_string = cJSON_PrintUnformatted(json);
end:
    cJSON_Delete(json);
    mysql_free_result(result);
    mysql_close(mysql);
    return json_string;
}

char *getUserPotholesBy14DaysJson(char *username, char *date)
{
    MYSQL *mysql = mysql_init(NULL);
    MYSQL_STMT *statement_potholes_days;
    MYSQL_BIND bind_user_param[1];
    MYSQL_BIND bind_columns[6];
    cJSON *json;
    cJSON *pothole;
    cJSON *lat;
    cJSON *lon;
    cJSON *address;
    cJSON *user;
    cJSON *timestamp;
    cJSON *intensity;
    double latitude, longitude;
    char address_[128];
    char username_[64];
    char timestamp_[32];
    int intensity_;

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

    // Initializing statement_potholes_days
    statement_potholes_days = mysql_stmt_init(mysql);

    if (statement_potholes_days != NULL)
        if (mysql_stmt_errno(statement_potholes_days) != 0)
        {
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_potholes_days));
            return 0;
        }

    ////Preparing statement_potholes_days
    mysql_stmt_prepare(statement_potholes_days, "SELECT  Latitude, Longitude, Address, User, Timestamp, Intensity FROM PotholeReport WHERE User = ?", 99);

    if (statement_potholes_days != NULL)
        if (mysql_stmt_errno(statement_potholes_days) != 0)
        {
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_potholes_days));
            return 0;
        }

    memset(bind_columns, 0, sizeof(bind_columns));

    // Setting latitude column
    bind_columns[0].buffer_type = MYSQL_TYPE_DOUBLE;
    bind_columns[0].buffer = &latitude;
    bind_columns[0].buffer_length = sizeof(double);

    // Setting longitude column
    bind_columns[1].buffer_type = MYSQL_TYPE_DOUBLE;
    bind_columns[1].buffer = &longitude;
    bind_columns[1].buffer_length = sizeof(double);

    // Setting Address column
    bind_columns[2].buffer_type = MYSQL_TYPE_STRING;
    bind_columns[2].buffer = address_;
    bind_columns[2].buffer_length = sizeof(address_);
    size_t address_length = strlen(address_);
    bind_columns[2].length = &address_length;
    // Setting User column
    bind_columns[3].buffer_type = MYSQL_TYPE_STRING;
    bind_columns[3].buffer = username_;
    bind_columns[3].buffer_length = sizeof(username_);
    size_t user_length = strlen(username_);
    bind_columns[3].length = &user_length;

    // Setting Timestamp column
    bind_columns[4].buffer_type = MYSQL_TYPE_STRING;
    bind_columns[4].buffer = timestamp_;
    bind_columns[4].buffer_length = sizeof(timestamp_);
    size_t timestamp_length = strlen(timestamp_);
    bind_columns[4].length = &timestamp_length;

    // Setting Intensity column
    bind_columns[5].buffer_type = MYSQL_TYPE_LONG;
    bind_columns[5].buffer = &intensity_;
    bind_columns[5].buffer_length = sizeof(intensity_);

    memset(bind_user_param, 0, sizeof(bind_user_param));
    // Setting User column
    bind_user_param[0].buffer_type = MYSQL_TYPE_STRING;
    bind_user_param[0].buffer = username;
    bind_user_param[0].buffer_length = sizeof(username);
    size_t user_length_1 = strlen(username);
    bind_user_param[0].length = &user_length_1;

    // Binding result buffer to statement_potholes_days
    mysql_stmt_bind_result(statement_potholes_days, bind_columns);
    if (statement_potholes_days != NULL)
        if (mysql_stmt_errno(statement_potholes_days) != 0)
        {
            fprintf(stderr, "mysql_stmt_bind_result() error: %s\n\n", mysql_stmt_error(statement_potholes_days));
            return 0;
        }

    // Binding user parameter to statement_potholes_days
    mysql_stmt_bind_param(statement_potholes_days, bind_user_param);
    if (statement_potholes_days != NULL)
        if (mysql_stmt_errno(statement_potholes_days) != 0)
        {
            fprintf(stderr, "mysql_stmt_bind_param() error: %s\n\n", mysql_stmt_error(statement_potholes_days));
            return 0;
        }

    // Executing statement
    mysql_stmt_execute(statement_potholes_days);
    if (statement_potholes_days != NULL)
        if (mysql_stmt_errno(statement_potholes_days) != 0)
        {
            fprintf(stderr, "mysql_stmt_execute() error: %s\n\n", mysql_stmt_error(statement_potholes_days));
            return 0;
        }

    // Creates array containing potholes
    json = cJSON_CreateArray();

    if (!json)
        goto end;

    // For each pothole in database
    while (!mysql_stmt_fetch(statement_potholes_days))
    {
        timestamp = cJSON_CreateString(timestamp_);

        if (!timestamp)
            goto end;
        int day_diff, mon_diff, year_diff;

        //
        if (getDateDifference(timestamp->valuestring, date, &day_diff, &mon_diff, &year_diff) == 1)
        {
            cJSON_Delete(timestamp);
            goto end;
        }
        // We only take potholes which have been reported in the last 14 days
        if (day_diff <= 14 && mon_diff == 0 && year_diff == 0)
        {

            pothole = cJSON_CreateObject();
            cJSON_AddItemToArray(json, pothole);
            lat = cJSON_CreateNumber(latitude);
            lon = cJSON_CreateNumber(longitude);
            address = cJSON_CreateString(address_);
            user = cJSON_CreateString(username_);
            intensity = cJSON_CreateNumber(intensity_);

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
end:
    cJSON_Delete(json);
    mysql_stmt_close(statement_potholes_days);
    mysql_close(mysql);
    return json_string;
}

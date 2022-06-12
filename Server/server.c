#include <stdio.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <sys/poll.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <errno.h>
#include <arpa/inet.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <netinet/in.h>
#include "Headers/thread-pool.h"
#include "Headers/logic.h"

#define SERVER_PORT 12345
#define BACKLOG 100
#define MAX_CONNECTIONS 200

#define FD_STOP_POLLING -2

extern int compress_array;
extern int num_threads_executing;
extern int task_count;

int main(int argc, char *argv[])
{
    // extern variables, defined in thread-pool.h and logic.h headers
    num_threads_executing = 0;
    task_count = 0;
    compress_array = FALSE;

    int polls = 0;
    int poll_err, read_bytes;
    int listen_sd = -1, new_sd = -1;
    int end_server = FALSE;
    struct sockaddr_in server_addr, client_addr;
    socklen_t client_addr_length;
    int timeout;
    int current_size = 0;
    pthread_t threads[THREADS_NUM];
    struct pollfd fds[MAX_CONNECTIONS];
    int nfds = 1;
    int flag1;
    socklen_t len = sizeof(flag1);

    printf("Creating listening socket..\n");
    if ((listen_sd = socket(PF_INET, SOCK_STREAM | SOCK_NONBLOCK, IPPROTO_TCP)) < 0) // Setting listening TCP ipv4 socket to non blocking
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
    printf("Setting TCP_KEEPIDLE TO %d seconds\n\n", flag1); // Setting TCP_KEEPIDLE TO 1800 seconds, time in which the server doesn't actually take any action to investigate on the possible dead peer.
    // After that time it sends an empty ack packet with ack flag set to 1
    if (setsockopt(listen_sd, SOL_TCP, TCP_KEEPIDLE, (void *)&flag1, len))
    {
        perror("ERROR: setsocketopt(), SO_KEEPIDLE");
        close(listen_sd), exit(EXIT_FAILURE);
    }

    flag1 = 5;
    printf("Setting TCP_KEEPCNT TO %d\n\n", flag1); // Setting TCP_KEEPCNT TO 5. This number indicates the maximum acks number that server can send to client without receiving response,
    // every TCP_KEEPCNT seconds and after TCP_KEEPIDLE. If the number of the acks sent without any response is 5 then client is considered to be dead and network resources can be freed.
    if (setsockopt(listen_sd, SOL_TCP, TCP_KEEPCNT, (void *)&flag1, len))
    {
        perror("ERROR: setsocketopt(), SO_KEEPCNT");
        close(listen_sd), exit(EXIT_FAILURE);
    }
    flag1 = 30;
    printf("Setting TCP_KEEPINTVL TO %d seconds\n\n", flag1); // Setting TCP_KEEPINTVL TO 30 seconds. If client didn't respond with an ack, then send another ack. This is done every 30 seconds for at most TCP_KEEPCNT times.
    if (setsockopt(listen_sd, SOL_TCP, TCP_KEEPINTVL, (void *)&flag1, len))
    {
        perror("ERROR: setsocketopt(), SO_KEEPINTVL");
        close(listen_sd), exit(EXIT_FAILURE);
    }

    memset(fds, 0, sizeof(fds));

    // Set up the listening socket in the pollfd array, events = POLLIN on listening socket means that we are only interested in receiving new connections from client peers
    fds[0].fd = listen_sd;
    fds[0].events = POLLIN;

    // Set up timeout to 12 seconds, after that, poll returns even if no file descriptor is ready
    timeout = (0.2 * 60 * 1000);

    // Inizializing Mutexes, Condition Variables and thread pool
    printf("INITIALIZING THREAD POOL AND GLOBAL VARIABLES...\n\n");
    initThreadPoolVariables();
    initializeThreadPool(threads, THREADS_NUM);

    printf("***SERVER SUCCESSFULLY STARTED***\n\n-----------------------------------------------------------------------------------------------------------\n\n");

    usleep(10000);
    // Loop waiting for incoming connections or for incoming data on any of the connected sockets
    do
    {

        printf("***n.%d POLL EXECUTED***\n\n", ++polls);
        if ((poll_err = poll(fds, nfds, timeout)) < 0)
        {
            perror("poll");
            break;
        }
        else if (poll_err == 0) // If poll timed out
        {
            if (compress_array == TRUE && num_threads_executing == 0) // If there are unused file descriptors we can shrink the pollfd struct array
            {
                compress_array = FALSE;
                for (int i = 0; i < nfds; i++)
                {
                    if (fds[i].fd == -1 || fds[i].fd == -2)

                    {
                        for (int j = i; j < nfds - 1; j++)
                        {
                            fds[j].fd = fds[j + 1].fd;
                        }
                        i--;
                        nfds--;
                    }
                }
                continue; // Jumps back to poll()
            }
        }
        printf("[-CLIENTS CONNECTED: %d -- THREADS EXECUTING TASKS: %d FILE DESCRIPTORS READY: %d\n-SOCKET DESCRIPTOR ARRAY:\n\n", nfds - 1, num_threads_executing, poll_err);
        for (int i = 0; i < nfds; i++)
        {
            printf("    fd_%d = %d", i, fds[i].fd);
        }
        printf("\n\n");

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
                    printf("---------- SHUTTING DOWN CONNECTION WITH ABSENT PEER WITH FD: %d ----------\n\n", fds[i].fd);
                    close(fds[i].fd);
                    fds[i].fd = -1;
                    // Remove unused file descriptors
                    for (int i = 0; i < nfds; i++)
                    {
                        if (fds[i].fd == -1 || fds[i].fd == -2)
                        {
                            for (int j = i; j < nfds - 1; j++)
                            {
                                fds[j].fd = fds[j + 1].fd;
                            }
                            i--;
                            nfds--;
                        }
                    }
                    continue;
                }
                end_server = TRUE; // If an unexpected event happened then we shut down server
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

                    // If server is full we stop accepting new connections
                    if (nfds == 200)
                    {
                        fprintf(stderr, "SERVER IS FULL, CONNECTION WITH IP TERMINATED: %s\n\n", inet_ntoa(client_addr.sin_addr));
                        close(new_sd);
                        break;
                    }

                    // Add the file descriptor of the new socket in the pollfd array
                    printf("NEW CONNECTION ACCEPTED IP: %s  FD: %d\n\n", inet_ntoa(client_addr.sin_addr), new_sd);
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
                // This temporary disables file descriptor, to avoid poll to return immediately even if a thread is already taking care of the task. Thread will reset the value as soon as everything has been read so that poll can listen to its events again
                fds[i].fd = FD_STOP_POLLING;
                addTask(read_task);
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

    // Releasing thread pool resources
    freeThreadPoolVariables();

    if (end_server)
        exit(EXIT_FAILURE);

    exit(EXIT_SUCCESS);
}
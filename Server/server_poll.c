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

#define SERVER_PORT 12345
#define BACKLOG 32

#define TRUE 1
#define FALSE 0

int main(int argc, char *argv[])
{
    int len, on = 1;
    int poll_err, read_bytes;
    int listen_sd = -1, new_sd = -1;
    int desc_ready, end_server = FALSE, compress_array = FALSE;
    int close_conn;
    char buffer[80];
    struct sockaddr_in server_addr, client_addr;
    socklen_t client_addr_length;
    int timeout;
    struct pollfd fds[200];
    int nfds = 1, current_size = 0;

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

    memset(fds, 0, sizeof(fds));

    // Set up the listening socket in the pollfd array, events = POLLIN means that we are only interested in receiving new connections with this socket
    fds[0].fd = listen_sd;
    fds[0].events = POLLIN;

    // Set up timeout to 3 minutes, after that, poll returns even if no file descriptor is ready
    timeout = (3 * 60 * 1000);

    // Loop waiting for incoming connections or for incoming data on any of the connected sockets
    do
    {
        printf("poll() executed\n");
        if ((poll_err = poll(fds, nfds, timeout)) < 0)
        {
            perror("poll");
            break;
        }
        else if (poll_err == 0)
        {
            fprintf(stderr, "poll() timed out\n");
            break;
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
                fprintf(stderr, "Unexpected event on file descriptor: %d\n", fds[i].revents);
                end_server = TRUE;
                break;
            }
            if (fds[i].fd == listen_sd)
            {
                printf("Listening socket is ready to accept new connections..\n");
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

                    printf("New connection accepted, IP: %s SD: %d\n", inet_ntoa(client_addr.sin_addr), new_sd);

                    // Add the file descriptor of the new socket in the pollfd array
                    fds[nfds].fd = new_sd;
                    fds[nfds].events = POLLIN;
                    nfds++;

                } while (1);
            }

            // Accepted socket is readable
            else
            {
                close_conn = FALSE;

                do
                {
                    printf("Accepted socket is ready to be read on, SD: %d\n", fds[i].fd);
                    // Read data from accepted socket, if EWOULDBLOCK is returned it means that there are no more bytes to read. Any other error code will cause the server to close the connection
                    read_bytes = recv(fds[i].fd, buffer, sizeof(buffer), 0);
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
                    printf("Closing connection associated to SD: %d\n", fds[i].fd);
                    close(fds[i].fd);
                    fds[i].fd = -1;
                    compress_array = TRUE;
                }
            }
        }

        // If compress_array flag was set, we are not interested in events on a certain file descriptor, we can then proceed to remove it from pollfd array
        if (compress_array)
        {
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

        compress_array = FALSE;

    } while (!end_server);

    // Closing all opened connections before shutting down server
    for (int i = 0; i < nfds; i++)
    {
        printf("Closing connection associated to SD: %d\n", fds[i].fd);
        if (fds[i].fd >= 0)
            close(fds[i].fd);
    }

    return 0;
}
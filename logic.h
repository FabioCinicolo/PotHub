#ifndef LOGIC_H
#define LOGIC_H

#define REPORT_POTHOLE 1
#define GET_POTHOLES_BY_RANGE 3
#define GET_USER_POTHOLES_BY_DAYS 2

#define TRUE 1
#define FALSE 0

typedef struct PollInfo
{
    int *old_fd;
    int fd;
    struct pollfd *fds;
} PollInfo;

typedef struct Pothole
{
    double latitude;
    double longitude;
    char *address;
    char *user;
    char *timestamp;
    int intensity;
} Pothole;

static char *opt_host_name = "database-1.cgk0rzhm3j8s.eu-south-1.rds.amazonaws.com"; /* host (default=localhost) */
static char *opt_user_name = "fab_umb";                                              /* username (default=login name)*/
static char *opt_password = "Ciao161998_";                                           /* password (default=none) */
static unsigned int opt_port_num = 0;                                                /* port number (use built-in) */
static char *opt_socket_name = NULL;                                                 /* socket name (use built-in) */
static char *opt_db_name = "PothubDatabase";                                         /* database name (default=none) */
static unsigned int opt_flags = 0;                                                   /* connection flags (none) */

int compress_array;

int doWork(void *);

int reportPothole(Pothole);

char *getPotholesByRangeJson(double latitude, double longitude, double range);

char *getUserPotholesBy14DaysJson(char *username, char *date);

#endif

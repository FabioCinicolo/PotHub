#ifndef THREAD_POOL_h
#define THREAD_POOL_h

#define THREADS_NUM 20
#define MAX_TASK_NUM 1000

typedef struct Task
{
    int (*work)(void *);
    void *args;
} Task;

Task task_queue[MAX_TASK_NUM];
pthread_mutex_t thread_pool_mutex;
pthread_mutex_t num_threads_executing_mutex;
pthread_cond_t thread_pool_cond_empty;
pthread_cond_t thread_pool_cond_full;

int num_threads_executing;
int task_count;

void *threadStarter(void *args);

void initializeThreadPool(pthread_t *threads, int thread_num);

void initThreadPoolVariables();

void joinThreads(pthread_t *threads, int thread_num);

void freeThreadPoolVariables();

void addTask(Task);

int executeTask(Task *);

#endif
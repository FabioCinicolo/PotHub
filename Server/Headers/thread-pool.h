#ifndef THREAD_POOL_h
#define THREAD_POOL_h

#define THREADS_NUM 4
#define MAX_TASK_NUM 100000


typedef struct Task
{
    int (*work)(void *);
    void *args;
} Task;


Task task_queue[MAX_TASK_NUM];
pthread_mutex_t thread_pool_mutex;
pthread_cond_t thread_pool_cond_empty;

pthread_mutex_t global_vars_mutex;
int num_threads_executing;
int task_count;

void *threadStarter(void *args);

void initializeThreadPool(pthread_t *threads, int thread_num);

void joinThreads(pthread_t *threads, int thread_num);

void addTask(Task);

int executeTask(Task *);

#endif

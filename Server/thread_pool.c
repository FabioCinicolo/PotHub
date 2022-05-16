#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <signal.h>

#define THREADS_NUM 4
#define MAX_TASK_NUM 20

typedef struct Task
{
    void (*work)(void *);
    void *args;
} Task;

Task task_queue[MAX_TASK_NUM];
int task_count = 0;
pthread_mutex_t thread_pool_mutex;
pthread_cond_t thread_pool_cond_empty;

void initializeThreadPool(pthread_t *threads, int thread_num);
void joinThreads(pthread_t *threads, int thread_num);
void addTask(Task task);
void executeTask(Task *task);
void print(void *args);
void godpig(void *args);

int main(int argc, char **argv)
{

    pthread_t threads[THREADS_NUM];

    pthread_mutex_init(&thread_pool_mutex, NULL);
    pthread_cond_init(&thread_pool_cond_empty, NULL);

    initializeThreadPool(threads, THREADS_NUM);

    //INSERT HERE TASKS
    
    joinThreads(threads, THREADS_NUM);

    pthread_mutex_destroy(&thread_pool_mutex);
    pthread_cond_destroy(&thread_pool_cond_empty);

    return 0;
}

void *threadStarter(void *args)
{

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
        executeTask(&task);
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

void executeTask(Task *task)
{
    task->work(task->args);
}

void print(void *x)
{
    printf("%d\n", *((int *)x));
}

void godpig(void *x)
{
    printf("Dinamically added %c\n", *((char *)x));
}

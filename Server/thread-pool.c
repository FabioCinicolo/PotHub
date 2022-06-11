#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include "Headers/thread-pool.h"

void *threadStarter(void *args)
{
    num_threads_executing = 0;
    task_count = 0;

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

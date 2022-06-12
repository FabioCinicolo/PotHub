#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include "Headers/thread-pool.h"

void *threadStarter(void *args) // Thread starter function
{
    printf("THREAD %ld RUNNING\n\n", pthread_self());
    while (1)
    {
        Task task;
        pthread_mutex_lock(&thread_pool_mutex);
        while (task_count == 0) // If task count is 0 then put thread in wait queue
            pthread_cond_wait(&thread_pool_cond_empty, &thread_pool_mutex);

        task = task_queue[0];                    // Assigns a task to the thread
        for (int i = 0; i < task_count - 1; i++) // Updates Task queue, removing old task
            task_queue[i] = task_queue[i + 1];
        task_count--;

        pthread_mutex_unlock(&thread_pool_mutex);
        pthread_cond_signal(&thread_pool_cond_full); // Signals possible sleeping thread that a new task can be added to queue
        int ret = executeTask(&task);                // Execute task
    }
}

void initializeThreadPool(pthread_t *threads, int thread_num) // Initializes thread pool with thread_num threads
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
    while (task_count == MAX_TASK_NUM) // If task queue is full then put thread in wait queue
        pthread_cond_wait(&thread_pool_cond_full, &thread_pool_mutex);

    task_queue[task_count++] = task; // Add task to task queue and increments by one task count
    pthread_mutex_unlock(&thread_pool_mutex);
    pthread_cond_signal(&thread_pool_cond_empty); // Signals possible sleeping thread that a new task can be taken from queue
}

int executeTask(Task *task) // Executes task and updates num_threads_executing
{
    pthread_mutex_lock(&num_threads_executing_mutex);
    num_threads_executing++;
    pthread_mutex_unlock(&num_threads_executing_mutex);
    int ret = task->work(task->args);
    pthread_mutex_lock(&num_threads_executing_mutex);
    num_threads_executing--;
    pthread_mutex_unlock(&num_threads_executing_mutex);
    return ret;
}

void freeThreadPoolVariables() // Releases thread pool resources
{

    pthread_mutex_destroy(&num_threads_executing_mutex);
    pthread_mutex_destroy(&thread_pool_mutex);
    pthread_cond_destroy(&thread_pool_cond_empty);
    pthread_cond_destroy(&thread_pool_cond_full);
}

void initThreadPoolVariables() // Initializes thread pool resources
{

    pthread_mutex_init(&thread_pool_mutex, NULL);
    pthread_cond_init(&thread_pool_cond_empty, NULL);
    pthread_cond_init(&thread_pool_cond_full, NULL);
    pthread_mutex_init(&num_threads_executing_mutex, NULL);
}
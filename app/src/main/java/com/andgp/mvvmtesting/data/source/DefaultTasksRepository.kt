package com.andgp.mvvmtesting.data.source

import androidx.lifecycle.LiveData
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 *  Created by Andres Gonzalez on 02/03/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Concrete implementation to load tasks from the data source into a cache
 */
class DefaultTasksRepository(
    //Dependencies
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksRepository {

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    updateTasksFromRemoteDataSource()
                } catch (e: Exception) {
                    return Result.Error(e)
                }
            }
            return tasksLocalDataSource.getTasks()
        }
    }

    override suspend fun refreshTasks() {
        wrapEspressoIdlingResource {
            updateTasksFromRemoteDataSource()
        }
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.observeTasks()
        }
    }

    override suspend fun refreshTask(taskId: String) {
        wrapEspressoIdlingResource {
            updateTaskFromRemoteDataSource(taskId)
        }
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.observeTask(taskId)
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                updateTaskFromRemoteDataSource(taskId)
            }
            return tasksLocalDataSource.getTask(taskId)
        }
    }

    override suspend fun saveTask(task: Task) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.saveTask(task) }
                launch { tasksLocalDataSource.saveTask(task) }
            }
        }
    }

    override suspend fun completeTask(task: Task) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.completeTask(task) }
                launch { tasksLocalDataSource.completeTask(task) }
            }
        }
    }

    override suspend fun completeTask(taskId: String) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.completeTask(taskId) }
                launch { tasksLocalDataSource.completeTask(taskId) }
            }
        }
    }

    override suspend fun activateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.activateTask(task) }
                launch { tasksLocalDataSource.activateTask(task) }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                (getTaskWithId(taskId) as? Result.Success)?.let { it ->
                    activateTask(it.data)
                }
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.clearCompletedTasks() }
                launch { tasksLocalDataSource.clearCompletedTasks() }
            }
        }
    }

    override suspend fun deleteAllTask() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                coroutineScope {
                    launch { tasksRemoteDataSource.deleteAllTasks() }
                    launch { tasksLocalDataSource.deleteAllTasks() }
                }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteTask(taskId) }
                launch { tasksLocalDataSource.deleteTask(taskId) }
            }
        }
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        wrapEspressoIdlingResource {
            val remoteTasks = tasksRemoteDataSource.getTasks()

            if (remoteTasks is Result.Success) {
                // Real apps might want to do a proper sync.
                tasksLocalDataSource.deleteAllTasks()
                remoteTasks.data.forEach { task ->
                    tasksLocalDataSource.saveTask(task)
                }
            } else if (remoteTasks is Result.Error) {
                throw remoteTasks.exception
            }
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        wrapEspressoIdlingResource {
            val remoteTask = tasksRemoteDataSource.getTask(taskId)

            if (remoteTask is Result.Success) {
                tasksLocalDataSource.saveTask(remoteTask.data)
            }
        }
    }

    private suspend fun getTaskWithId(id: String): Result<Task> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.getTask(id)
        }
    }
}
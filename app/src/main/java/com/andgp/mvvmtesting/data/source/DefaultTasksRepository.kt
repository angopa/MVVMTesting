package com.andgp.mvvmtesting.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.source.local.TasksLocalDataSource
import com.andgp.mvvmtesting.data.source.local.ToDoDatabase
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.*

/**
 *  Created by Andres Gonzalez on 02/03/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Concrete implementation to load tasks from the data source into a cache
 */
class DefaultTasksRepository private constructor(application: Application) {

    private val tasksRemoteDataSource: TasksDataSource
    private val tasksLocalDataSource: TasksDataSource
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    companion object {
        @Volatile
        private var INSTANCE: DefaultTasksRepository? = null

        fun getRepository(application: Application): DefaultTasksRepository {
            return INSTANCE ?: synchronized(this) {
                DefaultTasksRepository(application).also {
                    INSTANCE = it
                }
            }
        }
    }

    init {
        val database = Room.databaseBuilder(
            application.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        tasksRemoteDataSource = TasksRemoteDataSource
        tasksLocalDataSource = TasksLocalDataSource(database.getTaskDao())
    }

    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource()
            } catch (e: Exception) {
                return Result.Error(e)
            }
        }
        return tasksLocalDataSource.getTasks()
    }

    suspend fun refreshTasks() {
        updateTasksFromRemoteDataSource()
    }

    fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksLocalDataSource.observeTasks()
    }

    suspend fun refreshTask(taskId: String) {
        updateTaskFromRemoteDataSource(taskId)
    }

    fun observeTask(taskId: String): LiveData<Result<Task>> {
        return tasksLocalDataSource.observeTask(taskId)
    }

    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task> {
        if (forceUpdate) {
            updateTaskFromRemoteDataSource(taskId)
        }
        return tasksLocalDataSource.getTask(taskId)
    }

    suspend fun saveTask(task: Task) {
        coroutineScope {
            launch { tasksRemoteDataSource.saveTask(task) }
            launch { tasksLocalDataSource.saveTask(task) }
        }
    }

    suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { tasksRemoteDataSource.completeTask(task) }
            launch { tasksLocalDataSource.completeTask(task) }
        }
    }

    suspend fun completeTask(taskId: String) {
        coroutineScope {
            launch { tasksRemoteDataSource.completeTask(taskId) }
            launch { tasksLocalDataSource.completeTask(taskId) }
        }
    }

    suspend fun activateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        coroutineScope {
            launch { tasksRemoteDataSource.activateTask(task) }
            launch { tasksLocalDataSource.activateTask(task) }
        }
    }

    suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Result.Success)?.let { it ->
                activateTask(it.data)
            }
        }
    }

    suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { tasksRemoteDataSource.clearCompletedTasks() }
            launch { tasksLocalDataSource.clearCompletedTasks() }
        }
    }

    suspend fun deleteAllTask() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteAllTasks() }
                launch { tasksLocalDataSource.deleteAllTasks() }
            }
        }
    }

    suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { tasksRemoteDataSource.deleteTask(taskId) }
            launch { tasksLocalDataSource.deleteTask(taskId) }
        }
    }

    private suspend fun updateTasksFromRemoteDataSource() {
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

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        val remoteTask = tasksRemoteDataSource.getTask(taskId)

        if (remoteTask is Result.Success) {
            tasksLocalDataSource.saveTask(remoteTask.data)
        }
    }

    private suspend fun getTaskWithId(id: String): Result<Task> {
        return tasksLocalDataSource.getTask(id)
    }
}
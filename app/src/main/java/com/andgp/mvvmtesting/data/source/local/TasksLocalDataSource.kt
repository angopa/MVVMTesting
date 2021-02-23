package com.andgp.mvvmtesting.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.Result.Error
import com.andgp.mvvmtesting.data.Result.Success
import com.andgp.mvvmtesting.data.source.TasksDataSource
import com.andgp.mvvmtesting.data.source.model.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *  Created by Andres Gonzalez on 02/03/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Concrete implementation of a data source as a db.
 */
class TasksLocalDataSource(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return taskDao.observeTasks().map {
            Success(it)
        }
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return taskDao.observeTaskById(taskId).map {
            Success(it)
        }
    }

    override suspend fun refreshTasks() {
        //NO-OP
    }

    override suspend fun refreshTask(taskId: String) {
        //NO-OP
    }

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(taskDao.getTasks())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(Exception("Task Not Found!"))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        taskDao.insertTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        taskDao.updateCompleted(task.id, true)
    }

    override suspend fun completeTask(taskId: String) = withContext(ioDispatcher) {
        taskDao.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        taskDao.updateCompleted(task.id, false)
    }

    override suspend fun activateTask(taskId: String) = withContext(ioDispatcher) {
        taskDao.updateCompleted(taskId, false)
    }

    override suspend fun clearCompletedTasks() = withContext<Unit>(ioDispatcher) {
        taskDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        taskDao.deleteTasks()
    }

    override suspend fun deleteTask(taskId: String) = withContext<Unit>(ioDispatcher) {
        taskDao.deleteTaskById(taskId)
    }
}


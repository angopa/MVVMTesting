package com.andgp.mvvmtesting.data.source

import androidx.lifecycle.LiveData
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.source.model.Task

/**
 *  Created by Andres Gonzalez on 02/03/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Main entry point for accessing tasks data.
 */
interface TasksDataSource {
    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun getTasks(): Result<List<Task>>

    suspend fun refreshTasks()

    suspend fun refreshTask(taskId: String)

    fun observeTask(taskId: String): LiveData<Result<Task>>

    suspend fun getTask(taskId: String): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)
}
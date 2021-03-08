package com.andgp.mvvmtesting.data.source

import androidx.lifecycle.LiveData
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.source.model.Task

/**
 *  Created by Andres Gonzalez on 3/1/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 */
interface TasksRepository {
    suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>>

    suspend fun refreshTasks()

    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun refreshTask(taskId: String)

    fun observeTask(taskId: String): LiveData<Result<Task>>

    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTask()

    suspend fun deleteTask(taskId: String)
}
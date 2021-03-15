package com.andgp.mvvmtesting.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.source.model.Task
import kotlinx.coroutines.runBlocking

/**
 *  Created by Andres Gonzalez on 3/1/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 */
class FakeTestRepository : TasksRepository {
    val taskServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        this.shouldReturnError = value
    }

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks(true)
    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(taskServiceData.values.toList())
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return observableTasks
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        taskServiceData[taskId]?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Could not find task"))
    }

    override suspend fun saveTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(task: Task) {
        taskServiceData[task.id]?.isCompleted = true
        runBlocking { refreshTasks() }
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTask() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }

    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            taskServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }
}
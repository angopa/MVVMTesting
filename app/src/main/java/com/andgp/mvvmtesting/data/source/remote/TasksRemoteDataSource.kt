package com.andgp.mvvmtesting.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.Result.*
import com.andgp.mvvmtesting.data.source.TasksDataSource
import com.andgp.mvvmtesting.data.source.model.Task
import kotlinx.coroutines.delay

/**
 * Implementation of the data source that adds a latency simulating network.
 *
 */
object TasksRemoteDataSource : TasksDataSource {
    private const val SERVICE_LATENCY_IN_MILLIS = 2000L
    private var TASK_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    private val observableTasks = MutableLiveData<Result<List<Task>>>()


    override suspend fun refreshTasks() {
        observableTasks.value = this.getTasks()
    }

    override suspend fun refreshTask(taskId: String) {
        refreshTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return observableTasks
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return observableTasks.map { tasks ->
            when (tasks) {
                is Loading -> Loading
                is Error -> Error(tasks.exception)
                is Success -> {
                    val task = tasks.data.firstOrNull() { it.id == taskId }
                        ?: return@map Error(Exception("Not Found"))
                    Success(task)
                }
            }
        }
    }

    override suspend fun getTasks(): Result<List<Task>> {
        //Simulate network by delaying the execution.
        val tasks = TASK_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Success(tasks)
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        //Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        TASK_SERVICE_DATA[taskId]?.let {
            return Success(it)
        }
        return Error(Exception("Task Not Found"))
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description)
        TASK_SERVICE_DATA[newTask.id] = newTask
    }

    override suspend fun saveTask(task: Task) {
        TASK_SERVICE_DATA[task.id] = task
    }

    override suspend fun completeTask(task: Task) {
        val completeTask = Task(task.title, task.description, true, task.id)
        TASK_SERVICE_DATA[task.id] = completeTask
    }

    override suspend fun completeTask(taskId: String) {
        // Not required for the remote data source
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.id)
        TASK_SERVICE_DATA[task.id] = activeTask
    }

    override suspend fun activateTask(taskId: String) {
        // Not required for the remote data source
    }

    override suspend fun clearCompletedTasks() {
        TASK_SERVICE_DATA = TASK_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override suspend fun deleteAllTasks() {
        TASK_SERVICE_DATA.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        TASK_SERVICE_DATA.remove(taskId)
    }
}

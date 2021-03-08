package com.andgp.mvvmtesting.ui.addedittask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.source.TasksRepository
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.util.Event
import kotlinx.coroutines.launch

/**
 *  Created by Andres Gonzalez on 02/23/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 */
class AddEditTaskViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    //Two-way databinding, exposing MutableLiveData
    val title = MutableLiveData<String>()

    //Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText = _snackbarText

    private val _taskUpdateEvent = MutableLiveData<Event<Unit>>()
    val taskUpdateEvent = _taskUpdateEvent

    private var taskId: String? = null

    private var isNewTask: Boolean = false

    private var isDataLoaded = false

    private var taskCompleted = false

    fun start(taskId: String?) {
        if (_dataLoading.value == true) {
            return
        }

        this.taskId = taskId
        if (taskId == null) {
            // No need to populate, it's a new task
            this.isNewTask = true
            return
        }
        if (isDataLoaded) {
            // No need to populate, already have data
            return
        }

        isNewTask = false
        _dataLoading.value = true

        viewModelScope.launch {
            tasksRepository.getTask(taskId).let { result ->
                if (result is Result.Success) {
                    onTaskLoaded(result.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    private fun onTaskLoaded(task: Task) {
        title.value = task.title
        description.value = task.description
        taskCompleted = task.isCompleted
        _dataLoading.value = false
        isDataLoaded = true
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    // Called when clicking on fab
    fun saveTask() {
        val currentTitle = title.value
        val currentDescription = description.value

        if (currentTitle == null || currentDescription == null) {
            _snackbarText.value = Event(R.string.empty_task_message)
            return
        }
        if (Task(currentTitle, currentDescription).isEmpty) {
            _snackbarText.value = Event(R.string.empty_task_message)
            return
        }

        val currentTaskId = taskId
        if (isNewTask || currentTaskId == null) {
            createTask(Task(currentTitle, currentDescription))
        } else {
            val task = Task(currentTitle, currentDescription, taskCompleted, currentTaskId)
            updateTask(task)
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        tasksRepository.saveTask(task)
        _taskUpdateEvent.value = Event(Unit)
    }

    private fun updateTask(task: Task) {
        if (isNewTask) {
            throw RuntimeException("updateTask() was called but task is new.")
        }

        viewModelScope.launch {
            tasksRepository.saveTask(task)
            _taskUpdateEvent.value = Event(Unit)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AddEditViewModelFactory(
    private val repository: TasksRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        AddEditTaskViewModel(repository) as T

}
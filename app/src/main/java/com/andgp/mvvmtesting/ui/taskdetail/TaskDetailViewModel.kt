package com.andgp.mvvmtesting.ui.taskdetail

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.source.DefaultTasksRepository
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.util.Event
import kotlinx.coroutines.launch

/**
 *  Created by Andres Gonzalez on 2/25/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 */
class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val tasksRepository = DefaultTasksRepository.getRepository(application)

    private val _taskId = MutableLiveData<String>()

    private val _task = _taskId.switchMap { taskId ->
        tasksRepository.observeTask(taskId).map { computeResult(it) }
    }

    val task: LiveData<Task?> = _task

    val isDataAvailable: LiveData<Boolean> = _task.map { it != null }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editTaskEvent = MutableLiveData<Event<Unit>>()
    val editTaskEvent: LiveData<Event<Unit>> = _editTaskEvent

    private val _deleteTaskEvent = MutableLiveData<Event<Unit>>()
    val deleteTaskEvent: LiveData<Event<Unit>> = _deleteTaskEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    // This LiveData depends on another so we can use a transformation.
    val completed: LiveData<Boolean> = _task.map { input: Task? ->
        input?.isCompleted ?: false
    }

    fun start(taskId: String?) {
        // if we're already loading or already loaded, return (might be a config change)
        if (_dataLoading.value == true || taskId == _taskId.value) {
            return
        }

        //Trigger the load
        _taskId.value = taskId
    }

    fun deleteTask() = viewModelScope.launch {
        _taskId.value?.let {
            tasksRepository.deleteTask(it)
            _deleteTaskEvent.value = Event(Unit)
        }
    }

    fun editTask() {
        _editTaskEvent.value = Event(Unit)
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val task = _task.value ?: return@launch
        if (completed) {
            tasksRepository.completeTask(task)
            shownSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            shownSnackbarMessage(R.string.task_marked_active)
        }
    }

    fun refresh() {
        // Refresh the repository and the task will be updated automatically.
        _task.value?.let {
            _dataLoading.value = true
            viewModelScope.launch {
                tasksRepository.refreshTask(it.id)
                _dataLoading.value = false
            }
        }
    }

    private fun computeResult(taskResult: Result<Task>): Task? {
        return if (taskResult is Result.Success) {
            taskResult.data
        } else {
            shownSnackbarMessage(R.string.loading_task_error)
            null
        }
    }

    private fun shownSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}
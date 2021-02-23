package com.andgp.mvvmtesting.ui.tasks

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.Result.Success
import com.andgp.mvvmtesting.data.source.DefaultTasksRepository
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.ui.tasks.TasksFilterType.*
import com.andgp.mvvmtesting.util.Event
import kotlinx.coroutines.launch

/**
 *  Created by Andres Gonzalez on 02/03/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  ViewModel for the task list screen.
 */
class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val tasksRepository = DefaultTasksRepository.getRepository(application)

    private val _forceUpdate = MutableLiveData(false)

    private val _items: LiveData<List<Task>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                tasksRepository.refreshTasks()
                _dataLoading.value = false
            }
        }
        tasksRepository.observeTasks().switchMap { filterTasks(it) }
    }

    val items: LiveData<List<Task>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noTaskLabel = MutableLiveData<Int>()
    val noTaskLabel: LiveData<Int> = _noTaskLabel

    private val _noTaskIconRes = MutableLiveData<Int>()
    val noTaskIconRes: LiveData<Int> = _noTaskIconRes

    private val _tasksAddViewVisible = MutableLiveData<Boolean>()
    val tasksAddViewVisible: LiveData<Boolean> = _tasksAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    private var resultMessageShown: Boolean = false

    private var currentFiltering = ALL_TASK

    // This LiveData depends on another so we can use a transformation
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        //Set initial state
        setFiltering(ALL_TASK)
        loadTasks(true)
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be [TasksFilterType.ALL_TASK],
     * [TasksFilterType.ACTIVE_TASKS] or [TasksFilterType.COMPLETED_TASK]
     */
    fun setFiltering(requestType: TasksFilterType) {
        currentFiltering = requestType

        //Depending on the filter type, set the filtering label, icon drawable, etc.
        when (requestType) {
            ALL_TASK -> {
                setFilter(
                    R.string.label_all,
                    R.string.no_tasks_all,
                    R.drawable.logo_no_fill,
                    true
                )
            }
            ACTIVE_TASKS -> {
                setFilter(
                    R.string.label_active,
                    R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp,
                    false
                )
            }
            COMPLETED_TASK -> {
                setFilter(
                    R.string.label_completed,
                    R.string.no_task_completed,
                    R.drawable.ic_verified_user_96dp,
                    false
                )
            }
        }
        //Refresh List
        loadTasks(false)
    }

    private fun setFilter(
        @StringRes filteringLabelString: Int,
        @StringRes noTasksLabelString: Int,
        @DrawableRes noTaskIconDrawable: Int,
        tasksAddVisibility: Boolean
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noTaskLabel.value = noTasksLabelString
        _noTaskIconRes.value = noTaskIconDrawable
        _tasksAddViewVisible.value = tasksAddVisibility
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            tasksRepository.clearCompletedTasks()
            shownSnackbarMessage(R.string.completed_tasks_cleaned)
        }
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            tasksRepository.completeTask(task)
            shownSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            shownSnackbarMessage(R.string.task_marked_active)
        }
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    fun addNewTask() {
        _newTaskEvent.value = Event(Unit)
    }

    /**
     * Called by the Data Binding
     */
    fun openTask(taskId: String) {
        _openTaskEvent.value = Event(taskId)
    }

    fun showEditResultMessage(result: Int) {
        if (resultMessageShown) return
        when (result) {
            EDIT_RESULT_OK -> shownSnackbarMessage(R.string.successfully_saved_task_message)
            ADD_EDIT_RESULT_OK -> shownSnackbarMessage(R.string.successfully_addes_task_message)
            DELETE_RESULT_OK -> shownSnackbarMessage(R.string.duccessfully_deleted_task_message)
        }
        resultMessageShown = true
    }

    private fun shownSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun filterTasks(tasksResult: Result<List<Task>>): LiveData<List<Task>> {
        // TODO: This is a good case for liveData builder. Replace when stable.
        val result = MutableLiveData<List<Task>>()

        if (tasksResult is Success) {
            isDataLoadingError.value = false
            viewModelScope.launch {
                result.value = filterItems(tasksResult.data, currentFiltering)
            }
        } else {
            result.value = emptyList()
            shownSnackbarMessage(R.string.loading_task_error)
            isDataLoadingError.value = true
        }

        return result
    }

    /**
     * @param forceUpdate Pass in true to refresh the data in the [TasksDataSource]
     */
    fun loadTasks(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    private fun filterItems(tasks: List<Task>, filteringType: TasksFilterType): List<Task>? {
        val tasksToShow = ArrayList<Task>()

        //We filter the tasks based on the requested type
        for (task in tasks) {
            when (filteringType) {
                ALL_TASK -> tasksToShow.add(task)
                ACTIVE_TASKS -> if (task.isActive) tasksToShow.add(task)
                COMPLETED_TASK -> if (task.isCompleted) tasksToShow.add(task)
            }
        }
        return tasksToShow
    }

    fun refresh() {
        _forceUpdate.value = true
    }

}
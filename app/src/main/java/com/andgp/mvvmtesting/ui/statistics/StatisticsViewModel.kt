package com.andgp.mvvmtesting.ui.statistics

import androidx.lifecycle.*
import com.andgp.mvvmtesting.data.Result
import com.andgp.mvvmtesting.data.Result.Success
import com.andgp.mvvmtesting.data.source.TasksRepository
import com.andgp.mvvmtesting.data.source.model.Task
import kotlinx.coroutines.launch

/**
 *  Created by Andres Gonzalez on 2/26/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 */
class StatisticsViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    private val _dataLoading = MutableLiveData<Boolean>(false)
    val dataLoading = _dataLoading

    private val tasks: LiveData<Result<List<Task>>> = tasksRepository.observeTasks()

    private val stats: LiveData<StatsResult?> = tasks.map {
        if (it is Success) {
            getActiveAndCompleteState(it.data)
        } else {
            null
        }
    }

    val activeTasksPercent = stats.map {
        it?.activeTasksPercent ?: 0f
    }
    val completePercent = stats.map {
        it?.completeTasksPercent ?: 0f
    }

    val error: LiveData<Boolean> = tasks.map { it is Error }

    val empty: LiveData<Boolean> = tasks.map { (it as? Success)?.data.isNullOrEmpty() }

    fun refresh() {
        _dataLoading.value = true
        viewModelScope.launch {
            tasksRepository.refreshTasks()
            _dataLoading.value = false
        }
    }

}
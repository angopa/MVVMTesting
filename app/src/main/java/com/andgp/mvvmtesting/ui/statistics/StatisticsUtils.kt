package com.andgp.mvvmtesting.ui.statistics

import com.andgp.mvvmtesting.data.source.model.Task

/**
 *  Created by Andres Gonzalez on 2/26/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 *
 *  Function that does some trivial computation. Used to showcase unit tests.
 */
internal fun getActiveAndCompleteState(tasks: List<Task>?): StatsResult =
    if (tasks == null || tasks.isEmpty()) {
        StatsResult(
            0f,
            0f
        )
    } else {
        val totalTasks = tasks!!.size
        val numberOfActiveTasks = tasks.count { it.isActive }
        StatsResult(
            activeTasksPercent = 100f * numberOfActiveTasks / tasks.size,
            completeTasksPercent = 100f * (totalTasks - numberOfActiveTasks) / tasks.size
        )
    }

data class StatsResult(val activeTasksPercent: Float, val completeTasksPercent: Float)
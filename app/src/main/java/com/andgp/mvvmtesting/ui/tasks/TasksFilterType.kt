package com.andgp.mvvmtesting.ui.tasks

/**
 *  Created by Andres Gonzalez on 02/03/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 * Used with the filter spinner in the tasks list.
 */
enum class TasksFilterType {
    /**
     * Do not filter tasks
     */
    ALL_TASK,

    /**
     * Filters only the active (not completed yet) tasks
     */
    ACTIVE_TASKS,

    /**
     *  Filters only the completed tasks
     */
    COMPLETED_TASK
}
package com.andgp.mvvmtesting.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.andgp.mvvmtesting.data.source.model.Task

/**
 *  Created by Andres Gonzalez on 02/17/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Data Access Object for the tasks table.
 */
@Dao
interface TaskDao {

    /**
     * Observers list of tasks.
     *
     * @return all tasks
     */
    @Query("SELECT * FROM tasks")
    fun observeTasks(): LiveData<List<Task>>

    /**
     * Observes a single task.
     *
     * @param taskId the entryId
     * @return the task with taskId
     */
    @Query("SELECT * FROM tasks WHERE entryId = :taskId")
    fun observeTaskById(taskId: String): LiveData<Task>

    /**
     * Select all tasks from the task table
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM tasks")
    fun getTasks(): List<Task>

    /**
     * TODO
     *
     * @param taskId teh task entityId
     * @return the task with taskId
     */
    @Query("SELECT * FROM tasks WHERE entryId = :taskId")
    fun getTaskById(taskId: String): Task?

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    /**
     * Update a task.
     *
     * @param task task to be updated
     * @return  the number of tasks updated. this should always be 1.
     */
    @Update
    suspend fun updateTask(task: Task): Int

    /**
     * Update the completed status of a task
     *
     * @param taskId id of the task
     * @param completed status to be updated
     */
    @Query("UPDATE tasks SET completed = :completed WHERE entryId= :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean)

    /**
     * Delete task by id.
     *
     * @param taskId id of the task
     * @return te number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM tasks WHERE entryId = :taskId")
    suspend fun deleteTaskById(taskId: String): Int

    /**
     * TODO: WARNING: Be sure you know what you are doing before use this.
     *
     * Delete all tasks
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteTasks()

    /**
     * Delete all completed tasks from the table
     *
     * @return the number of tasks deleted
     */
    @Query("DELETE FROM tasks WHERE completed = 1")
    suspend fun deleteCompletedTasks(): Int
}
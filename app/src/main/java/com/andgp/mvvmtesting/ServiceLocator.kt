package com.andgp.mvvmtesting

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.andgp.mvvmtesting.data.source.DefaultTasksRepository
import com.andgp.mvvmtesting.data.source.TasksDataSource
import com.andgp.mvvmtesting.data.source.TasksRepository
import com.andgp.mvvmtesting.data.source.local.TasksLocalDataSource
import com.andgp.mvvmtesting.data.source.local.ToDoDatabase
import com.andgp.mvvmtesting.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

/**
 *  Created by Andres Gonzalez on 3/3/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 */
object ServiceLocator {
    private var dataBase: ToDoDatabase? = null

    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    private val lock = Any()

    fun provideTaskRepository(context: Context): TasksRepository =
        synchronized(this) {
            return tasksRepository ?: createTaskRepository(context)
        }

    private fun createTaskRepository(context: Context): TasksRepository {
        val newRepo =
            DefaultTasksRepository(
                TasksRemoteDataSource,
                createTaskLocalDataSource(context)
            )
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = dataBase ?: createDataBase(context)
        return TasksLocalDataSource(database.getTaskDao())
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java,
            "Task.db"
        ).build()
        dataBase = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            //Clear all data to avoid test pollution
            dataBase?.apply {
                clearAllTables()
                close()
            }
            dataBase = null
            tasksRepository = null
        }
    }
}
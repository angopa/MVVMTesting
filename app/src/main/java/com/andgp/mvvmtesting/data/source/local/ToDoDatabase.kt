package com.andgp.mvvmtesting.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andgp.mvvmtesting.data.source.model.Task

/**
 *  Created by Andres Gonzalez on 02/17/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Note that exportSchema should be true in production databases.
 */
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
}
package com.andgp.mvvmtesting.data.source.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 *  Created by Andres Gonzalez on 02/17/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Immutable model class for a Task. In order to compile with Room, we can't use @JvmOverloads to
 *  generate multiple constructors.
 *
 * @property title          title of the task
 * @property description    description of the task
 * @property isCompleted    whether or not this task is completed
 * @property id             id of the task
 */
@Entity(tableName = "tasks")
data class Task @JvmOverloads constructor(
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "completed") var isCompleted: Boolean = false,
    @PrimaryKey @ColumnInfo(name = "entryId") var id: String = UUID.randomUUID().toString()
) {
    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}
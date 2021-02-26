package com.andgp.mvvmtesting.ui.tasks

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andgp.mvvmtesting.data.source.model.Task

/**
 *  Created by Andres Gonzalez on 02/18/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  [BindingAdpater]s for the [Task]s list
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Task>?) {
    items?.let {
        (listView.adapter as TaskAdapter).submitList(it)
    }
}

@BindingAdapter("app:completedTask")
fun setStyle(textView: TextView, enabled: Boolean) {
    if (enabled) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}
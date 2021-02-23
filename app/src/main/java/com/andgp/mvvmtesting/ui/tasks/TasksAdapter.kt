package com.andgp.mvvmtesting.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.databinding.TaskItemBinding

/**
 *  Created by Andres Gonzalez on 02/18/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Adapter for the task list. Has a reference to the [TasksViewModel] to send actions back to it.
 */
class TaskAdapter(private val viewModel: TasksViewModel) :
    ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(viewModel, getItem(position))

    class ViewHolder private constructor(val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: TasksViewModel, item: Task) {
            binding.viewModel = viewModel
            binding.task = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old and a new
 * list that's been passed to 'submitList'
 *
 */
class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

}
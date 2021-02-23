package com.andgp.mvvmtesting.ui.tasks

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.databinding.FragmentTasksBinding
import com.andgp.mvvmtesting.util.Event.*
import com.andgp.mvvmtesting.util.setupRefreshLayout
import com.andgp.mvvmtesting.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 *  Created by Andres Gonzalez on 02/03/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 *
 */
class TasksFragment : Fragment() {
    private val viewModel by viewModels<TasksViewModel>()
    private val args: TasksFragmentArgs by navArgs()

    private lateinit var viewDataBinding: FragmentTasksBinding

    private lateinit var listAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentTasksBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_tasks_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadTasks(true)
                true
            }
            else -> false
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.tasksList)
        setupNavigation()
        setupFab()
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(this.viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })
        viewModel.newTaskEvent.observe(this.viewLifecycleOwner, EventObserver {
            navigateToAddNewTask()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks_menu, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASK
                        else -> TasksFilterType.ALL_TASK
                    }
                )
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_task_fab)?.let {
            it.setOnClickListener { navigateToAddNewTask() }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
            null,
            "New Task"
        )
        findNavController().navigate(action)
    }

    private fun openTaskDetails(taskId: String) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailsFragment(
            taskId
        )
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TaskAdapter(viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}
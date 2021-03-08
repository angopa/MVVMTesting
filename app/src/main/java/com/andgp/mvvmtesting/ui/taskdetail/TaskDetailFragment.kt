package com.andgp.mvvmtesting.ui.taskdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andgp.mvvmtesting.MVVMTestingApplication
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.data.source.DefaultTasksRepository
import com.andgp.mvvmtesting.data.source.TasksRepository
import com.andgp.mvvmtesting.databinding.FragmentTaskDetailBinding
import com.andgp.mvvmtesting.ui.tasks.DELETE_RESULT_OK
import com.andgp.mvvmtesting.util.Event
import com.andgp.mvvmtesting.util.setupRefreshLayout
import com.andgp.mvvmtesting.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

/**
 *  Created by Andres Gonzalez on 2/25/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 */
class TaskDetailFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentTaskDetailBinding

    private val args: TaskDetailFragmentArgs by navArgs()

    private val viewModel by viewModels<TaskDetailViewModel> {
        TaskDetailViewModelFactory((requireContext().applicationContext as MVVMTestingApplication).tasksRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_detail, container, false)
        viewDataBinding = FragmentTaskDetailBinding.bind(view).apply {
            viewmodel = viewModel
        }.also {
            it.lifecycleOwner = this.viewLifecycleOwner
        }

        viewModel.start(args.taskId)

        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFab()
        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteTask()
                true
            }
            else -> false
        }
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe(viewLifecycleOwner, Event.EventObserver {
            val action = TaskDetailFragmentDirections
                .actionTaskDetailsFragmentToTaskFragment(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })

        viewModel.editTaskEvent.observe(viewLifecycleOwner, Event.EventObserver {
            val action = TaskDetailFragmentDirections
                .actionTaskDetailsFragmentToAddEditTaskFragment(
                    args.taskId,
                    "Edit Task"
                )
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        activity?.findViewById<View>(R.id.edit_task_fab)?.setOnClickListener {
            viewModel.editTask()
        }
    }
}
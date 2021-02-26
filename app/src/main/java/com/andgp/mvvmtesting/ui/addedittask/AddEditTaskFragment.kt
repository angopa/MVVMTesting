package com.andgp.mvvmtesting.ui.addedittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.databinding.FragmentAddEditTaskBinding
import com.andgp.mvvmtesting.ui.tasks.ADD_EDIT_RESULT_OK
import com.andgp.mvvmtesting.util.Event
import com.andgp.mvvmtesting.util.setupRefreshLayout
import com.andgp.mvvmtesting.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

/**
 *  Created by Andres Gonzalez on 02/23/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 */
class AddEditTaskFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentAddEditTaskBinding

    private val args: AddEditTaskFragmentArgs by navArgs()

    private val viewModel by viewModels<AddEditTaskViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_edit_task, container, false)
        viewDataBinding = FragmentAddEditTaskBinding.bind(root).apply {
            viewmodel = viewModel
        }
        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSnackbar()
        setupNavigation()
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
        viewModel.start(args.taskId)
    }

    private fun setupNavigation() {
        viewModel.taskUpdateEvent.observe(viewLifecycleOwner, Event.EventObserver {
            val action: NavDirections = AddEditTaskFragmentDirections
                .actionAddEditTaskFragmentToTasksFragment(ADD_EDIT_RESULT_OK)
            findNavController().navigate(action)
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }


}
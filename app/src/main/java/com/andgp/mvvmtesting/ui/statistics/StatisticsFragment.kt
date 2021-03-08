package com.andgp.mvvmtesting.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andgp.mvvmtesting.MVVMTestingApplication
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.data.source.TasksRepository
import com.andgp.mvvmtesting.databinding.FragmentStatisticsBinding
import com.andgp.mvvmtesting.util.setupRefreshLayout

/**
 *  Created by Andres Gonzalez on 2/26/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 */
class StatisticsFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentStatisticsBinding

    private val viewModel by viewModels<StatisticsViewModel> {
        StatisticsViewModelFactory(
            (requireContext().applicationContext as MVVMTestingApplication).tasksRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_statistics,
            container,
            false
        )
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.let {
            it.viewmodel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        this.setupRefreshLayout(viewDataBinding.refreshLayout)

    }
}

@Suppress("UNCHECKED_CAST")
class StatisticsViewModelFactory(
    private val repository: TasksRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        StatisticsViewModel(repository) as T

}
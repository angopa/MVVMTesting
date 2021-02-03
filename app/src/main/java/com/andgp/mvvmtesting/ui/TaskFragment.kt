package com.andgp.mvvmtesting.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

/**
 *  Created by Andres Gonzalez on 02/03/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 */
class TaskFragment : Fragment() {
    private val viewModel by viewModels<TaskViewModel>()

}
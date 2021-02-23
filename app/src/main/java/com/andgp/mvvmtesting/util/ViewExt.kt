package com.andgp.mvvmtesting.util

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.ui.views.ScrollChildSwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar

/**
 *  Created by Andres Gonzalez on 02/18/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 * Extension functions and Binding Adapters.
 */

/**
 * Transform static java function Snackbar.make() to an extension function on View.
 *
 * @param text
 * @param timeLength
 */
fun View.showSnackbar(text: String, timeLength: Int) {
    Snackbar.make(this, text, timeLength).run {
        show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified
 *
 * @param lifecycleOwner
 * @param snackbarEvent
 * @param timeLength
 */
fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {
    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(context.getString(it), timeLength)
        }
    })
}

fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
        ContextCompat.getColor(requireActivity(), R.color.colorAccent),
        ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
    )

    //Set The scrolling view in the custom SwipeRefreshLayout
    scrollUpChild?.let {
        refreshLayout.scrollIpChild = it
    }
}

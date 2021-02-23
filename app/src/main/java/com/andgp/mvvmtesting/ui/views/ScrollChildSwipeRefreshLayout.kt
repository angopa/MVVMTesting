package com.andgp.mvvmtesting.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 *  Created by Andres Gonzalez on 02/18/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  [SwipeRefreshLayout] works as expected when a scroll view is a direct child: it triggers
 *  the refresh only when the view is on top. This class adds a way (@link #setScrollUpChild} to
 *  define which view controls this behavior.
 */
class ScrollChildSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null
) : SwipeRefreshLayout(context, attributes) {

    var scrollIpChild: View? = null

    override fun canChildScrollUp(): Boolean =
        scrollIpChild?.canScrollVertically(-1) ?: super.canChildScrollUp()
}
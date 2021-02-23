package com.andgp.mvvmtesting.util

import androidx.lifecycle.Observer

/**
 *  Created by Andres Gonzalez on 02/07/2021.
 *  Copyright (c) 2021 City Electric Supply. All rights reserved.
 *
 *  Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
class Event<out T>(private val content: T) {


    var hasBeenHandled = false
        private set //Allow external read but not write

    /**
     * Return the content and prevents its use again
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Return the content, even if it's already been handled
     */
    fun peekContent(): T = content


    /**
     *  and [Observer] for [Event]s, simplifying the pattern of checking if the [Event]s content has
     *  already been handled.
     *
     *  [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled
     */
    class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
        override fun onChanged(event: Event<T>?) {
            event?.getContentIfNotHandled()?.let {
                onEventUnhandledContent(it)
            }
        }
    }
}
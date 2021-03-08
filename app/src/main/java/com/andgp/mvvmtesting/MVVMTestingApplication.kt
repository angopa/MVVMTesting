package com.andgp.mvvmtesting

import timber.log.Timber

/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 */
class MVVMTestingApplication : BaseApplication() {

    val tasksRepository
        get() = ServiceLocator.provideTaskRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (isDebug) Timber.plant(Timber.DebugTree())
    }
}
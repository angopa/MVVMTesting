package com.andgp.mvvmtesting.ui.tasks

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.andgp.mvvmtesting.DataBindingIdlingResource
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.ServiceLocator
import com.andgp.mvvmtesting.data.source.TasksRepository
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.monitorActivity
import com.andgp.mvvmtesting.util.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TaskActivityTest {
    lateinit var repository: TasksRepository

    //An idling Resource that waits for Data Binding to have no pending idling.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setup() {
        repository = ServiceLocator.provideTaskRepository(getApplicationContext())
        runBlocking { repository.deleteAllTask() }
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    //Idling resources tell Espresso that the app is idle or busy. This is needed when operations
    //are not schedule in the main Looper (for example when executed on a different thread)
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    //Unregister your Idling resource so it can be garbage collected and does not leak any memory
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun editTask() = runBlocking {
        //Set initial state
        repository.saveTask(Task("title1", "Description1"))

        //Start up Task screen
        val activityScenario = ActivityScenario.launch(TaskActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click on the task on the list and verify that all the data is correct
        onView(withText("title1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("title1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("Description1")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        //Click on the edit button, edit and save
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_tab)).perform(click())

        //Verify task is displayed on screen in the task list
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        //Verify previous task is not displayed
        onView(withText("TITLE1")).check(doesNotExist())

        //Make sure the activity is close before resetting the db
        activityScenario.close()
    }


}
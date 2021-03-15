package com.andgp.mvvmtesting

import android.app.Activity
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.andgp.mvvmtesting.data.source.TasksRepository
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.ui.tasks.TaskActivity
import com.andgp.mvvmtesting.util.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    private lateinit var repository: TasksRepository

    //An idling Resource that waits for Data Binding to have no pending idling.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setup() {
        repository = ServiceLocator.provideTaskRepository(getApplicationContext())
    }

    @After
    fun tearDown() {
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
    fun taskScreen_clickOnDrawerIcon_opensNavigation() {
        //Start the task screen
        val activityScenario = ActivityScenario.launch(TaskActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //1. Check if that the drawer is closed at startup
        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.START)))

        //2. Open drawer by clicking drawer icon
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        //3. Check if drawer is open
        onView(withId(R.id.drawer_layout)).check(matches(isOpen(Gravity.START)))

        //When using ActivityScenario.launch(), always call close()
        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleUpButton() = runBlocking {
        val task = Task("Up Button", "Description")
        repository.saveTask(task)

        //Start the task screen
        val activityScenario = ActivityScenario.launch(TaskActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //1. Click on the task on the list
        onView(withText("Up Button")).perform(click())

        //2. Click on the edit task button
        onView(withId(R.id.edit_task_fab)).perform(click())

        //3. Confirm that if we click Up button once, we end up back at the task details page
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        //4. Confirm that if we click Up button a second time, we end up back at the home screen
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))

        //When using ActivityScenario.launch(), always call close()
        activityScenario.close()
    }

    @Test
    fun taskDetailScreen_doubleBackButton() = runBlocking {
        val task = Task("Back Button", "Description")
        repository.saveTask(task)

        //Start Tasks screen
        val activityScenario = ActivityScenario.launch(TaskActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //1. Click on the task on the list
        onView(withText("Back Button")).perform(click())

        //2. Click on the Edit Task button
        onView(withId(R.id.edit_task_fab)).perform(click())

        //3. Confirm that if we click Back once, we end up back at the task details page.
        pressBack()
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

        //4. Confirm that if we click Back second time, we end up back at the home screen.
        pressBack()
        onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))

        //When using ActivityScenario.launch(). always call close()
        activityScenario.close()
    }
}

fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription(): String {
    var description = ""
    onActivity {
        description = it.findViewById<Toolbar>(R.id.toolbar).navigationContentDescription as String
    }
    return description
}
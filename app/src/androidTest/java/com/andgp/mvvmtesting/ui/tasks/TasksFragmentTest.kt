package com.andgp.mvvmtesting.ui.tasks

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.ServiceLocator
import com.andgp.mvvmtesting.data.source.FakeAndroidTestRepository
import com.andgp.mvvmtesting.data.source.TasksRepository
import com.andgp.mvvmtesting.data.source.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksFragmentTest {
    private lateinit var repository: TasksRepository

    val navController = mock(NavController::class.java)

    @Before
    fun setUp() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun tearDown() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {
        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
        repository.saveTask(Task("TITLE2", "DESCRIPTION2", false, "id2"))

        // Given - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        //WHEN - Click on the firstitem of the list
        onView(withId(R.id.tasks_list))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText("TITLE1")), click()
                )
            )

        //THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailsFragment("id1")
        )
    }
}
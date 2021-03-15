package com.andgp.mvvmtesting.ui.tasks

import com.andgp.mvvmtesting.MainCoroutineRule
import com.andgp.mvvmtesting.R
import com.andgp.mvvmtesting.data.source.FakeTestRepository
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.getOrAwaitValue
import com.andgp.mvvmtesting.util.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class TasksViewModelTest {

    //SUT
    private lateinit var tasksViewModel: TasksViewModel

    //Use fake repository to be injected into the viewModel
    private lateinit var taskRepository: FakeTestRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    private val mainCoRule = MainCoroutineRule()

    @Before
    fun setUp() {
        //We initialise the tasks to 3, with one active and two completed
        taskRepository = FakeTestRepository()
        val task1 = Task("Title", "Description")
        val task2 = Task("Title2", "Description2", isCompleted = true)
        val task3 = Task("Title3", "Description3", isCompleted = true)
        taskRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(taskRepository)
    }

    @Test
    fun completeTask_dataAndSnackbarUpdate() {
        //Create an active Task and add it to the repository
        val task = Task("Title", "Description")
        taskRepository.addTasks(task)

        //Mark the task as completed task
        tasksViewModel.completeTask(task, true)

        //Verify the task is completed
        assertThat(taskRepository.taskServiceData[task.id]?.isCompleted, `is`(true))

        //Assert that the snackbar has been update with the correct text
        val snackbarText: Event<Int> = tasksViewModel.snackbarText.getOrAwaitValue {}

        assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
    }
}
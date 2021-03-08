package com.andgp.mvvmtesting.ui.taskdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andgp.mvvmtesting.data.source.FakeTestRepository
import com.andgp.mvvmtesting.getOrAwaitValue
import com.andgp.mvvmtesting.ui.tasks.TasksFilterType
import com.andgp.mvvmtesting.ui.tasks.TasksViewModel
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class TaskDetailViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test (SUT)
    private lateinit var tasksViewModel: TasksViewModel

    @Before
    fun setup() {
        tasksViewModel = TasksViewModel(FakeTestRepository())
    }

    @Test
    fun addNewTask_setNewTaskEvent() {
        //Given a fresh viewModel

        //When adding a new task
        tasksViewModel.addNewTask()

        //Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.value
        assertThat(value?.getContentIfNotHandled(), not(nullValue()))
    }

    @Test
    fun setFilterAllTasks_taskAddViewVisible() {
        //When the filter type is ALL_TASK
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASK)

        // Then the 'Add Task' action is visible
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitValue {
            // Do nothing for this
        }
        assertThat(value, `is`(true))
    }
}

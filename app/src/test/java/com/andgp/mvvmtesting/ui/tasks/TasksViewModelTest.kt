package com.andgp.mvvmtesting.ui.tasks

import android.os.Build
import com.andgp.mvvmtesting.data.source.FakeTestRepository
import com.andgp.mvvmtesting.data.source.model.Task
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
class TasksViewModelTest {

    //SUT
    private lateinit var tasksViewModel: TasksViewModel

    //Use fake repository to be injected into the viewModel
    private lateinit var taskRepository: FakeTestRepository

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
    fun temp() {

    }
}
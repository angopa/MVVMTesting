package com.andgp.mvvmtesting.ui.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andgp.mvvmtesting.MainCoroutineRule
import com.andgp.mvvmtesting.data.source.FakeTestRepository
import com.andgp.mvvmtesting.data.source.model.Task
import com.andgp.mvvmtesting.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class StatisticsViewModelTest {

    //Executes each task synchronously using Architecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //Set the main coroutines dispatcher for unit testing
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    //Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    //Use a fake repository to be injected into the view model
    private lateinit var tasksRepository: FakeTestRepository

    @Before
    fun setup() {
        //initialize the repository with no tasks
        tasksRepository = FakeTestRepository()
        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @Test
    fun loadTasks_loading() {
        //pause dispatcher so you can verify initial values
        mainCoroutineRule.pauseDispatcher()

        //Load the task in the view model
        statisticsViewModel.refresh()

        //Than assert that the progress indicator is shown
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue {}, `is`(true))

        //Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        //Then assert that the progress indicator is hidden
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue {}, `is`(false))
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_emptyAndErrorTrue() {
        //Add Task to repository
        tasksRepository.addTasks(Task("title", "description", isCompleted = false))

        //Then empty and error are true (which triggers an error message to be shown)
        assertThat(statisticsViewModel.empty.getOrAwaitValue {}, `is`(true))
        assertThat(statisticsViewModel.error.getOrAwaitValue {}, `is`(true))
    }


    @Test
    fun getActiveAndCompleteStats_noCompleted_returnsHundredZero() {
        //Create an activity task
        val tasks = listOf<Task>(Task("title", "description", isCompleted = false))

        //Call the result
        val result = getActiveAndCompleteState(tasks)

        //Check the result
        assertThat(result.completeTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompleteStats_completed_returnsZeroHundred() {
        //Given
        val tasks = listOf<Task>(Task("title", "descriptions", isCompleted = true))

        //When
        val result = getActiveAndCompleteState(tasks)

        //Then
        assertThat(result.completeTasksPercent, `is`(100f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompleteStats_empty_returnsZeros() {
        //When
        val result = getActiveAndCompleteState(emptyList())

        //Then
        assertThat(result.completeTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        //When
        val result = getActiveAndCompleteState(null)

        //Then
        assertThat(result.completeTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_both_returnsFortySixty() {
        //Given
        val tasks = listOf(
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false)
        )

        //When
        val result = getActiveAndCompleteState(tasks)

        //Then
        Assert.assertThat(result.activeTasksPercent, `is`(40f))
        Assert.assertThat(result.completeTasksPercent, `is`(60f))
    }
}

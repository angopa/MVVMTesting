package com.andgp.mvvmtesting.ui.statistics

import com.andgp.mvvmtesting.data.source.model.Task
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class StatisticsViewModelTest {
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

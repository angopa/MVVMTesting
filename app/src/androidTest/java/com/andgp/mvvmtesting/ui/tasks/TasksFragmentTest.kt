package com.andgp.mvvmtesting.ui.tasks

import androidx.test.ext.junit.runners.AndroidJUnit4

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksFragmentTest {
//    private lateinit var repository: TasksRepository
//
//    val navController = mock(NavController::class.java)
//
//    @Before
//    fun setUp() {
//        repository = FakeAndroidTestRepository()
//        ServiceLocator.tasksRepository = repository
//    }
//
//    @After
//    fun tearDown() = runBlockingTest {
//        ServiceLocator.resetRepository()
//    }
//
//    @Test
//    fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {
//        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
//        repository.saveTask(Task("TITLE2", "DESCRIPTION2", false, "id2"))
//
//        // Given - On the home screen
//        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
//        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }
//
//        //WHEN - Click on the firstitem of the list
//        onView(withId(R.id.tasks_list))
//            .perform(
//                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
//                    hasDescendant(withText("TITLE1")), click()
//                )
//            )
//
//        //THEN - Verify that we navigate to the first detail screen
//        verify(navController).navigate(
//            TasksFragmentDirections.actionTasksFragmentToTaskDetailsFragment("id1")
//        )
//    }
}
package com.tomerpacific.todo

import androidx.test.core.app.ApplicationProvider
import com.tomerpacific.todo.view.MainViewModel
import com.tomerpacific.todo.view.TodoEvent
import com.tomerpacific.todo.view.TodoState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initialStateTest() = runTest {
        viewModel.state.value.let { todoState ->
            assert(todoState.todoItems.isEmpty())
            assert(!todoState.isAddingTodo)
            assert(todoState.todoListTitle.isEmpty())
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addTodoItemTest() = runTest {

        val results = mutableListOf<TodoState>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {
                results.add(it)
            }
        }

        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))

        assert(results[0].todoItemDescription.isEmpty())
        Thread.sleep(20)
        assert(results[1].todoItemDescription.isNotEmpty())
        assert(results[1].todoItemDescription.equals(TEST_TODO_ITEM_DESCRIPTION))
        job.cancel()
    }
}
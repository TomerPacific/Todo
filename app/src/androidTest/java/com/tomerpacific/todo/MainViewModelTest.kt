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


private const val TEST_TODO_ITEM_LIST_TITLE = "My Todo Items"
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        viewModel.clearAllTodoItems()
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
            viewModel.state.collect { todoState ->
                    results.add(todoState)
                }
            }

        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))

        assert(results[0].todoItemDescription.isEmpty())
        Thread.sleep(20)
        assert(results[1].todoItemDescription.isNotEmpty())
        assert(results[1].todoItemDescription == TEST_TODO_ITEM_DESCRIPTION)
        viewModel.onEvent(TodoEvent.SaveTodo)
        Thread.sleep(20)
        assert(results[2].todoItemDescription.isEmpty())
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeTodoItemTest() = runTest {
        val results = mutableListOf<TodoState>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { todoState ->
                    results.add(todoState)
                }
            }


        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))

        assert(results[results.size - 1].todoItemDescription.isEmpty())
        Thread.sleep(20)
        assert(results[results.size - 1].todoItemDescription.isNotEmpty())
        assert(results[results.size - 1].todoItemDescription == TEST_TODO_ITEM_DESCRIPTION)
        viewModel.onEvent(TodoEvent.SaveTodo)
        Thread.sleep(20)
        assert(results[results.size - 1].todoItemDescription.isEmpty())

        val todoItem = results[results.size - 1].todoItems[0]
        viewModel.onEvent(TodoEvent.DeleteTodo(todoItem))
        Thread.sleep(20)
        assert(results[results.size - 1].todoItems.isEmpty())
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setTodoListTitleTest() = runTest {

        val results = mutableListOf<TodoState>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { todoState ->
                results.add(todoState)
            }
        }

        viewModel.onEvent(TodoEvent.SetTodoListTitle(TEST_TODO_ITEM_LIST_TITLE))
        assert(results[0].todoListTitle.isEmpty())

        Thread.sleep(20)

        assert(results[1].todoListTitle.isNotEmpty())
        assert(results[1].todoListTitle == TEST_TODO_ITEM_LIST_TITLE)
        job.cancel()
    }

}
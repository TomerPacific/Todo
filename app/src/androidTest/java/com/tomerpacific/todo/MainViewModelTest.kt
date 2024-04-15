package com.tomerpacific.todo

import androidx.test.core.app.ApplicationProvider
import com.tomerpacific.todo.view.MainViewModel
import com.tomerpacific.todo.view.TodoEvent
import com.tomerpacific.todo.view.TodoState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
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

        val results = Channel<TodoState>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { todoState ->
                    results.send(todoState)
                }
            }

        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))

        val initialStateResult = results.receive()
        assert(initialStateResult.todoItemDescription.isEmpty())
        val afterSettingTodoDescriptionResult = results.receive()
        assert(afterSettingTodoDescriptionResult.todoItemDescription.isNotEmpty())
        assert(afterSettingTodoDescriptionResult.todoItemDescription == TEST_TODO_ITEM_DESCRIPTION)

        viewModel.onEvent(TodoEvent.SaveTodo)
        val afterSavingTodoResult = results.receive()
        assert(afterSavingTodoResult.todoItemDescription.isEmpty())
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeTodoItemTest() = runTest {
        val results = Channel<TodoState>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { todoState ->
                    results.send(todoState)
                }
            }


        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))

        val initialStateResult = results.receive()

        assert(initialStateResult.todoItemDescription.isEmpty())

        val afterSettingTodoDescriptionResult = results.receive()

        assert(afterSettingTodoDescriptionResult.todoItemDescription.isNotEmpty())
        assert(afterSettingTodoDescriptionResult.todoItemDescription == TEST_TODO_ITEM_DESCRIPTION)

        viewModel.onEvent(TodoEvent.SaveTodo)

        val afterSavingTodoResult = results.receive()
        assert(afterSavingTodoResult.todoItemDescription.isEmpty())

        val todoItem = afterSavingTodoResult.todoItems[0]

        viewModel.onEvent(TodoEvent.DeleteTodo(todoItem))
        val afterRemovingTodoResult = results.receive()
        assert(afterRemovingTodoResult.todoItems.isEmpty())

        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setTodoListTitleTest() = runTest {

        val results = Channel<TodoState>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { todoState ->
                results.send(todoState)
            }
        }

        viewModel.onEvent(TodoEvent.SetTodoListTitle(TEST_TODO_ITEM_LIST_TITLE))

        val initialStateResult = results.receive()

        assert(initialStateResult.todoListTitle.isEmpty())

        val afterSettingTodoListTitleResult = results.receive()

        assert(afterSettingTodoListTitleResult.todoListTitle.isNotEmpty())
        assert(afterSettingTodoListTitleResult.todoListTitle == TEST_TODO_ITEM_LIST_TITLE)
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addDuplicateTodoTest() = runTest {

        val results = mutableListOf<TodoState>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { todoState ->
                results.add(todoState)
            }
        }

        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))

        assert(results[0].todoItemDescription.isEmpty())
        Thread.sleep(20)
        assert(results[results.size - 1].todoItemDescription.isNotEmpty())
        assert(results[results.size - 1].todoItemDescription == TEST_TODO_ITEM_DESCRIPTION)
        viewModel.onEvent(TodoEvent.SaveTodo)
        Thread.sleep(20)
        assert(results[results.size - 1].todoItemDescription.isEmpty())
        Thread.sleep(20)
        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))
        Thread.sleep(20)
        assert(results[results.size - 1].isTodoItemADuplicate)
        
        job.cancel()
    }

}
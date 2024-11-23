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

        var afterSavingTodoResult = results.receive()

        assert(afterSavingTodoResult.todoItemDescription.isEmpty())

        while (afterSavingTodoResult.todoItems.isEmpty()) {
            afterSavingTodoResult = results.receive()
        }

        val todoItem = afterSavingTodoResult.todoItems[0]

        viewModel.onEvent(TodoEvent.DeleteTodo(todoItem))
        var afterRemovingTodoResult = results.receive()

        while (afterRemovingTodoResult.todoItems.isNotEmpty()) {
            afterRemovingTodoResult = results.receive()
        }

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

        val results = Channel<TodoState>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { todoState ->
                results.send(todoState)
            }
        }

        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))

        val initialStateResult = results.receive()

        assert(initialStateResult.todoItemDescription.isEmpty())

        val afterSettingTodoListTitleResult = results.receive()

        assert(afterSettingTodoListTitleResult.todoItemDescription.isNotEmpty())
        assert(afterSettingTodoListTitleResult.todoItemDescription == TEST_TODO_ITEM_DESCRIPTION)
        viewModel.onEvent(TodoEvent.SaveTodo)

        var afterSavingTodoResult = results.receive()

        assert(afterSavingTodoResult.todoItemDescription.isEmpty())

        while (afterSavingTodoResult.todoItems.isEmpty()) {
            afterSavingTodoResult = results.receive()
        }

        assert(afterSavingTodoResult.todoItemDescription.isEmpty())


        viewModel.onEvent(TodoEvent.SetTodoDescription(TEST_TODO_ITEM_DESCRIPTION))

        val afterSettingTodoDescriptionResult = results.receive()

        assert(afterSettingTodoDescriptionResult.isTodoItemADuplicate)
        
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun removeAllTodosTest() = runTest {
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

        var afterSavingTodoResult = results.receive()

        assert(afterSavingTodoResult.todoItemDescription.isEmpty())

        while (afterSavingTodoResult.todoItems.isEmpty()) {
            afterSavingTodoResult = results.receive()
        }

        viewModel.onEvent(TodoEvent.RemoveAllTodos)


        val afterRemovingAllTodosResult = results.receive()

        assert(afterRemovingAllTodosResult.todoItems.isEmpty())

        job.cancel()
    }

}
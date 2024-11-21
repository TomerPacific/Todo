package com.tomerpacific.todo.view

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.todo.TodoItem
import com.tomerpacific.todo.TodoItems
import com.tomerpacific.todo.TodoListPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import service.TodoItemsRepository
import service.TodoItemsSerializer
import service.TodoListPreferencesRepository
import service.todoListPreferencesDatastore
import java.util.UUID


private const val DATA_STORE_FILE_NAME = "todoItemsList.pb"

private val Context.todoItemsStore: DataStore<TodoItems> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = TodoItemsSerializer,
)

class MainViewModel(application: Application) : ViewModel() {

    private val todoItemsRepository: TodoItemsRepository =
        TodoItemsRepository(application.todoItemsStore)
    private val todoListPreferencesRepository: TodoListPreferencesRepository =
        TodoListPreferencesRepository(application.todoListPreferencesDatastore)

    private val _state = MutableStateFlow(TodoState())
    private var todoItemsAlreadyAdded = mutableListOf<TodoItem>()

    private val _todoItemsFlow = todoItemsRepository.todoItemsFlow
    private val _todoPreferencesFlow = todoListPreferencesRepository.todoListPreferencesFlow
    val state: StateFlow<TodoState> =
        combine(_state, _todoItemsFlow, _todoPreferencesFlow) { state: TodoState,
                                                                todoItemsFlow: TodoItems,
                                                                todoPreferencesFlow: TodoListPreferences ->

            val todoItems = todoItemsFlow.itemsList
            val todoListTitle = todoPreferencesFlow.title

            todoItemsAlreadyAdded = when (todoItems.isEmpty()) {
                true -> mutableListOf()
                false -> todoItems as MutableList<TodoItem>
            }

            state.copy(
                todoItems = todoItems as List<TodoItem>,
                todoListTitle = todoListTitle
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodoState())

    fun onEvent(event: TodoEvent) {
        when (event) {
            is TodoEvent.DeleteTodo -> {
                viewModelScope.launch {
                    todoItemsRepository.removeTodoItem(event.todo)
                }
            }

            is TodoEvent.HideAddTodoDialog -> {
                _state.update {
                    it.copy(
                        isAddingTodo = false
                    )
                }
            }

            is TodoEvent.SaveTodo -> {

                val itemDescription: String = state.value.todoItemDescription

                if (itemDescription.isEmpty() || state.value.isTodoItemADuplicate) {
                    return
                }

                val todoItem: TodoItem = TodoItem
                    .newBuilder()
                    .setItemId(UUID.randomUUID().toString())
                    .setItemDescription(itemDescription).build()

                viewModelScope.launch {
                    todoItemsRepository.updateTodoItems(todoItem)
                }

                _state.update {
                    it.copy(
                        isAddingTodo = false,
                        todoItemDescription = ""
                    )
                }
            }

            is TodoEvent.SetTodoDescription -> {

                _state.update {
                    it.copy(
                        todoItemDescription = event.todoDescription,
                        isTodoItemADuplicate = isDuplicateTodo(event.todoDescription)
                    )
                }
            }

            is TodoEvent.ShowAddTodoDialog -> {
                _state.update {
                    it.copy(
                        isAddingTodo = true
                    )
                }
            }

            is TodoEvent.SetTodoListTitle -> {

                viewModelScope.launch {
                    todoListPreferencesRepository.updateTodoListTitle(event.todoListTitle)
                }

                _state.update {
                    it.copy(
                        todoListTitle = event.todoListTitle
                    )
                }
            }

            is TodoEvent.RemoveAllTodos -> {
                viewModelScope.launch {
                    todoItemsRepository.removeAllTodoItems()
                }
            }
        }
    }

    fun clearAllTodoItems() {
        viewModelScope.launch {
            todoItemsRepository.removeAllTodoItems()
            _state.update {
                it.copy(
                    todoItems = listOf()
                )
            }
        }
    }

    private fun isDuplicateTodo(todoDescription: String): Boolean {
        return todoItemsAlreadyAdded.any { todoItem ->
            todoItem.itemDescription == todoDescription
        }
    }
}
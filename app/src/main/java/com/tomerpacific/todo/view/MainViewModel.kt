package com.tomerpacific.todo.view

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.todo.TodoItem
import com.tomerpacific.todo.TodoItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val todoItemsRepository: TodoItemsRepository = TodoItemsRepository(application.todoItemsStore)
    private val todoListPreferencesRepository: TodoListPreferencesRepository = TodoListPreferencesRepository(application.todoListPreferencesDatastore)
    private val _state = MutableStateFlow(TodoState())
    private var _todoItems = listOf<TodoItem>()
    val state: StateFlow<TodoState> = _state.asStateFlow()

    init {
        getTodoItems()
        getTodoListPreferences()
    }

    fun onEvent(event: TodoEvent) {
        when(event) {
            is TodoEvent.DeleteTodo -> {

            }
            is TodoEvent.HideAddTodoDialog -> {
                _state.update { it.copy(
                    isAddingTodo = false
                ) }
            }
            is TodoEvent.SaveTodo -> {

                val itemDescription: String = state.value.todoItemDescription
                if (itemDescription.isBlank()) {
                    return
                }

                val todoItem: TodoItem = TodoItem
                    .newBuilder()
                    .setItemId(UUID.randomUUID().toString())
                    .setItemDescription(itemDescription).build()

                viewModelScope.launch {
                    todoItemsRepository.updateTodoItems(todoItem)
                }

                _state.update { it.copy(
                    isAddingTodo = false,
                    todoItemDescription = ""
                ) }
            }
            is TodoEvent.SetTodoDescription -> {
                _state.update { it.copy(
                    todoItemDescription = event.todoDescription
                ) }
            }
            is TodoEvent.ShowAddTodoDialog ->  {
                _state.update { it.copy(
                    isAddingTodo = true
                ) }
            }
            is TodoEvent.SetTodoListTitle -> {
                _state.update { it.copy(
                    todoListTitle = event.todoListTitle
                ) }
                viewModelScope.launch {
                    todoListPreferencesRepository.updateTodoListTitle(state.value.todoListTitle)
                }
            }
        }
    }

    private fun getTodoItems() {
        viewModelScope.launch {
            val todoItems = todoItemsRepository.fetchCachedTodoItems()
            if (todoItems.itemsList.isNotEmpty()) {
                _todoItems = todoItems.itemsList
                _state.update { it.copy(
                    todoItems = _todoItems
                ) }
            }
        }
    }

    private fun getTodoListPreferences() {
        viewModelScope.launch {
            val todoListPreferences = todoListPreferencesRepository.fetchCachedTodoListPreferences()
            if (todoListPreferences.title.isNotEmpty()) {
                _state.update { it.copy(
                    todoListTitle = todoListPreferences.title
                )}
            }
        }
    }

    fun removeTodoItem(todoItemToRemove: TodoItem) {
        viewModelScope.launch {
            todoItemsRepository.removeTodoItem(todoItemToRemove)
        }
    }
}
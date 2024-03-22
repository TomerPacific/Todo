package com.tomerpacific.todo.view

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomerpacific.todo.TodoItem
import com.tomerpacific.todo.TodoItems
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

class MainViewModel(application: Application): ViewModel() {

    private val todoItemsRepository: TodoItemsRepository = TodoItemsRepository(application.todoItemsStore)
    private val todoListPreferencesRepository: TodoListPreferencesRepository = TodoListPreferencesRepository(application.todoListPreferencesDatastore)
    private val _state = MutableStateFlow(TodoState())
    private val _todoItems = todoItemsRepository.todoItemsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList<TodoItem>())
    val state: StateFlow<TodoState> = combine(_state, _todoItems) { state, todoItems ->
        val items = when (todoItems) {
            is List<*> -> todoItems
            is TodoItems -> todoItems.itemsList
            else -> listOf()
        }
        state.copy(
            todoItems = items as List<TodoItem>
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodoState())

    init {
        getTodoListPreferences()
    }

    fun onEvent(event: TodoEvent) {
        when(event) {
            is TodoEvent.DeleteTodo -> {
                viewModelScope.launch {
                    todoItemsRepository.removeTodoItem(event.todo)
                }
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

                if (isDuplicateTodo(itemDescription)) {
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

    fun clearAllTodoItems() {
        viewModelScope.launch {
            todoItemsRepository.removeAllTodoItems()
            _state.update { it.copy(
                todoItems = listOf()
            )}
        }

    }

    private fun isDuplicateTodo(todoDescription: String): Boolean {
        return todoItemsRepository.doesTodoAlreadyExist(todoDescription)
    }
}
package com.tomerpacific.todo.view

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tomerpacific.todo.TodoItem
import com.tomerpacific.todo.TodoItems
import com.tomerpacific.todo.TodoListPreferences
import kotlinx.coroutines.flow.MutableStateFlow
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

    val todoItemsFlow: LiveData<TodoItems> = todoItemsRepository.todoItemsFlow.asLiveData()

    val todoListPreferencesFlow: LiveData<TodoListPreferences> = todoListPreferencesRepository.todoListPreferencesFlow.asLiveData()

    private val _state = MutableStateFlow(TodoState())

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

                val itemDescription: String = _state.value.todoItemDescription
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
            }
            is TodoEvent.SetTodoDescription -> {
                _state.update { it.copy(
                    todoItemDescription = it.todoItemDescription
                ) }
            }
            is TodoEvent.ShowAddTodoDialog ->  {
                _state.update { it.copy(
                    isAddingTodo = true
                ) }
            }
        }
    }

    private fun getTodoItems() {
        viewModelScope.launch {
            todoItemsRepository.fetchCachedTodoItems()
        }
    }

    private fun getTodoListPreferences() {
        viewModelScope.launch {
            todoListPreferencesRepository.fetchCachedTodoListPreferences()
        }
    }

    fun addTodoItem(todoItemDescription: String) {
        val todoItem = TodoItem.newBuilder().setItemId(UUID.randomUUID().toString())
            .setItemDescription(todoItemDescription).build()
        viewModelScope.launch {
            todoItemsRepository.updateTodoItems(todoItem)
        }
    }

    fun removeTodoItem(todoItemToRemove: TodoItem) {
        viewModelScope.launch {
            todoItemsRepository.removeTodoItem(todoItemToRemove)
        }
    }

    fun updateTodoListTitle(todoListTitle: String) {
        viewModelScope.launch {
            todoListPreferencesRepository.updateTodoListTitle(todoListTitle)
        }
    }


}
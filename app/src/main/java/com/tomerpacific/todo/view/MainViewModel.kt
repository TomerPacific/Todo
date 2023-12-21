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
    init {
        getTodoItems()
        getTodoListPreferences()
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
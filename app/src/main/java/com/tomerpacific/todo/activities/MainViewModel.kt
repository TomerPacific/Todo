package com.tomerpacific.todo.activities

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
import kotlinx.coroutines.launch
import service.TodoItemsRepository
import service.TodoItemsSerializer
import java.util.UUID


private const val DATA_STORE_FILE_NAME = "todoItemsList.proto"

private val Context.todoItemsStore: DataStore<TodoItems> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = TodoItemsSerializer,
)

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val todoItemsRepository: TodoItemsRepository = TodoItemsRepository(application.todoItemsStore)

    val todoItemsFlow: LiveData<TodoItems> = todoItemsRepository.todoItemsFlow.asLiveData()
    init {
        getTodoItems()
    }

    private fun getTodoItems() {
        viewModelScope.launch {
            todoItemsRepository.fetchCachedTodoItems()
        }
    }

    fun addTodoItem(todoItemDescription: String) {
        val todoItem = TodoItem.newBuilder().setItemId(UUID.randomUUID().toString())
            .setItemDescription(todoItemDescription).build()
        viewModelScope.launch {
            todoItemsRepository.updateTodoItems(todoItem)
        }
    }


}
package com.tomerpacific.todo.activities

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tomerpacific.todo.TodoItems
import kotlinx.coroutines.launch
import service.TodoItemsRepository
import service.TodoItemsSerializer


private const val DATA_STORE_FILE_NAME = "todoItemsList.proto"

private val Context.todoItemsStore: DataStore<TodoItems> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = TodoItemsSerializer,
)

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val todoItemsRepository: TodoItemsRepository = TodoItemsRepository(application.todoItemsStore)
    private val _todoItems: MutableLiveData<TodoItems> = MutableLiveData()
    val todoItems: LiveData<TodoItems> = _todoItems

    init {
        getTodoItems()
    }
    private fun getTodoItems() {
        viewModelScope.launch {
            _todoItems.value = todoItemsRepository.fetchCachedTodoItems()
        }
    }


}
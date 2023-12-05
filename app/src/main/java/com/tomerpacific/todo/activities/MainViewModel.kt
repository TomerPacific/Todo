package com.tomerpacific.todo.activities

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.AndroidViewModel
import com.tomerpacific.todo.TodoItems
import service.TodoItemsRepository
import service.TodoItemsSerializer


private const val DATA_STORE_FILE_NAME = "todoItemsList.proto"

private val Context.todoItemsStore: DataStore<TodoItems> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = TodoItemsSerializer,
)

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val todoItemsRepository: TodoItemsRepository = TodoItemsRepository(application.todoItemsStore)


}
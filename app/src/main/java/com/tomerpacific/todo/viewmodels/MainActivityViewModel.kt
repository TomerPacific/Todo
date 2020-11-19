package com.tomerpacific.todo.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.repositories.TodoRepository
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var mTodoData : MutableLiveData<List<TodoData>> = MutableLiveData()
    private var todoDataList: List<TodoData> = listOf()
    private var applicationContext : Application = application

    init {
        if (TodoRepository.isSavingInSharedPreferences()) {
            todoDataList = TodoRepository.getTodoDataFromSharedPreferences(applicationContext)
            mTodoData.value = todoDataList
        } else {
            TodoRepository.getTodoDataFromDb(::onFetchDataFromBackendSuccess, ::onFetchDataFromBackendFailure)
        }
    }

    fun getTodoData() : LiveData<List<TodoData>> {
        return mTodoData
    }

    fun addTodo(todoTask: TodoData) {
        val data : MutableList<TodoData> = todoDataList.toMutableList()
        data.add(todoTask)
        todoDataList = Collections.unmodifiableList(data)
        mTodoData.value = todoDataList
        TodoRepository.updateTodoData(applicationContext, todoTask)
    }

    fun saveTodoData(context: Context) {
        TodoRepository.saveTodoDataInSession(context)
    }

    fun removeAllTodoData(context: Context) {
        TodoRepository.removeTodoData(context)
        todoDataList = listOf()
        mTodoData.value = todoDataList
    }

    fun removeTodoItem(context: Context, todoItemToRemove: TodoData) {
        TodoRepository.removeTodoItem(context, todoItemToRemove)
    }

    private fun onFetchDataFromBackendSuccess(res : List<TodoData>) {
        todoDataList = res
        mTodoData.value = todoDataList

    }

    private fun onFetchDataFromBackendFailure(error : String) {
        todoDataList = listOf()
        mTodoData.value = todoDataList
    }

}
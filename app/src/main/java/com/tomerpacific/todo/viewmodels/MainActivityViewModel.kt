package com.tomerpacific.todo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.repositories.TodoRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var mTodoData : MutableLiveData<List<TodoData>> = MutableLiveData()
    private var applicationContext : Application = application

    init {
        mTodoData = TodoRepository.getTodoData(applicationContext)

    }

    fun getTodoData() : LiveData<List<TodoData>> {
        return mTodoData
    }

    fun addTodo(todoTask: TodoData) {
        val data : MutableList<TodoData> = mTodoData.value.orEmpty().toMutableList()
        data.add(todoTask)
        mTodoData.postValue(data)
        TodoRepository.updateTodoData(applicationContext, todoTask)
    }

}
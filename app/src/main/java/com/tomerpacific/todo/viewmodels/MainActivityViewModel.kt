package com.tomerpacific.todo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.repositories.TodoRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var mTodoData : MutableLiveData<List<TodoData>> = MutableLiveData()

    init {
        mTodoData = TodoRepository.getTodoData(application.applicationContext)
    }

    fun getTodoData() : LiveData<List<TodoData>> {
        return mTodoData
    }

    fun addTodo(todoTask: TodoData) {
        var data : List<TodoData> = mTodoData.value.orEmpty()
        data = data as MutableList
        data.add(todoTask)
        mTodoData.postValue(data)
    }

}
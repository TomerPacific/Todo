package com.tomerpacific.todo.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomerpacific.todo.models.TodoData

class MainActivityViewModel : ViewModel() {

    private var mTodoData : MutableLiveData<List<TodoData>> = MutableLiveData()

}
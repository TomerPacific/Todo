package com.tomerpacific.todo.repositories

import androidx.lifecycle.MutableLiveData
import com.tomerpacific.todo.models.TodoData

object TodoRepository {

    private var todoData : ArrayList<TodoData> = ArrayList()

    fun getTodoData(): MutableLiveData<List<TodoData>> {
        var data = MutableLiveData<List<TodoData>>()
        data.value = todoData

        return data
    }

}
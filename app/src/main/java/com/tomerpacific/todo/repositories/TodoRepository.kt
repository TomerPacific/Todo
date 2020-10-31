package com.tomerpacific.todo.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tomerpacific.todo.DataSavingManager
import com.tomerpacific.todo.models.TodoData

object TodoRepository {

    private var todoData : ArrayList<TodoData> = ArrayList()

    fun getTodoData(context: Context): MutableLiveData<List<TodoData>> {
        setTodoData(context)
        var data = MutableLiveData<List<TodoData>>()
        data.value = todoData

        return data
    }

    private fun setTodoData(context: Context) {
        DataSavingManager.fetchTodoData(context)
    }

}
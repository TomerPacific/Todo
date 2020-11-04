package com.tomerpacific.todo.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.services.TodoDataSharedPreferencesService
import com.tomerpacific.todo.services.TodoDatabaseService

object TodoRepository {

    private var todoData : List<TodoData> = listOf()

    fun getTodoData(context: Context): MutableLiveData<List<TodoData>> {
        setTodoData(context)
        var data = MutableLiveData<List<TodoData>>()
        data.value = todoData

        return data
    }

    private fun setTodoData(context: Context) {
        val isTodoDataSavedOnDevice = TodoDataSharedPreferencesService.instance.didUserDecideToSaveDataInSharedPreferences(context)

        if (isTodoDataSavedOnDevice) {
            todoData = TodoDataSharedPreferencesService.instance.getTodoData(context)
            return
        }

        TodoDatabaseService.instance.fetchTodoDataFromDB(TodoRepository::onFetchDataFromBackendSuccess,
                                                         TodoRepository::onFetchDataFromBackendFailure)
    }

    fun onFetchDataFromBackendSuccess(res : List<TodoData>) {
        todoData = res
    }

    fun onFetchDataFromBackendFailure(error : String) {
        todoData = listOf()
    }

}
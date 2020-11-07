package com.tomerpacific.todo.repositories

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.services.TodoDataSharedPreferencesService
import com.tomerpacific.todo.services.TodoDatabaseService

object TodoRepository {

    private var todoData : List<TodoData> = listOf()

    fun getTodoData(context: Context): MutableLiveData<List<TodoData>> {
        setTodoData(context)
        val data = MutableLiveData<List<TodoData>>()
        data.value = todoData

        return data
    }

    fun updateTodoData(application: Application, todoTask: TodoData) {
        val isTodoDataSavedOnDevice = TodoDataSharedPreferencesService.instance.didUserDecideToSaveDataInSharedPreferences(application)

        val mutableTodoData : MutableList<TodoData> = todoData.toMutableList()
        mutableTodoData.add(todoTask)

        when(isTodoDataSavedOnDevice) {
            true -> TodoDataSharedPreferencesService.instance.saveTodoDataToSharedPreferences(application, mutableTodoData)
            false -> TodoDatabaseService.instance.updateTodoDataInDB(application, mutableTodoData)
        }
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

    private fun onFetchDataFromBackendSuccess(res : List<TodoData>) {
        todoData = res
    }

    private fun onFetchDataFromBackendFailure(error : String) {
        todoData = listOf()
    }

}
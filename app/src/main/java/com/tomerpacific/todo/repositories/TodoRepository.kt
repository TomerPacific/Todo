package com.tomerpacific.todo.repositories

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tomerpacific.todo.TodoConstants
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.services.TodoDataSharedPreferencesService
import com.tomerpacific.todo.services.TodoDatabaseService

object TodoRepository {

    private var todoData : List<TodoData> = listOf()
    private var didSaveDataInSharedPreferences : Boolean? = null

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

    fun decideOnUserDataSavingFlow(context: Context) {

        if (didSaveDataInSharedPreferences != null) {
            return
        }

        val sharedPref = context.getSharedPreferences(
            TodoConstants.TODO_SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        val shouldSaveDataInSharedPreferences: String? = sharedPref.getString(
            TodoConstants.SAVE_DATA_PREFERENCE_KEY,
            TodoConstants.SAVE_DATA_ON_DEVICE
        )

        didSaveDataInSharedPreferences =
            when(shouldSaveDataInSharedPreferences) {
                TodoConstants.SAVE_DATA_ON_DEVICE -> true
                else -> false
            }
    }

    fun isSavingInSharedPreferences() : Boolean {
        return (didSaveDataInSharedPreferences != null && didSaveDataInSharedPreferences == true)
    }

    private fun onFetchDataFromBackendSuccess(res : List<TodoData>) {
        todoData = res

    }

    private fun onFetchDataFromBackendFailure(error : String) {
        todoData = listOf()
    }

}
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
    private var didSaveDataInSharedPreferences : Boolean = false
    private var didAlreadySetSavePreferencesFlag : Boolean = false

    fun getTodoDataFromSharedPreferences(context: Context): List<TodoData> {
        todoData = TodoDataSharedPreferencesService.instance.getTodoData(context)
        return todoData
    }

    fun getTodoDataFromDb(success : (data: List<TodoData>) -> Unit, failure: (error:String) -> Unit) {
        TodoDatabaseService.instance.fetchTodoDataFromDB(success, failure)
    }

    fun updateTodoData(application: Application, todoTask: TodoData) {
        val isTodoDataSavedOnDevice = TodoDataSharedPreferencesService.instance.didUserDecideToSaveDataInSharedPreferences(application)

        val mutableTodoData : MutableList<TodoData> = todoData.toMutableList()
        mutableTodoData.add(todoTask)
        todoData = mutableTodoData.toList()

        when(isTodoDataSavedOnDevice) {
            true -> TodoDataSharedPreferencesService.instance.saveTodoDataToSharedPreferences(application, mutableTodoData)
            false -> TodoDatabaseService.instance.updateTodoDataInDB(application, mutableTodoData)
        }
    }


    fun decideOnUserDataSavingFlow(context: Context) {

        if (didAlreadySetSavePreferencesFlag) {
            return;
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

        didAlreadySetSavePreferencesFlag = true
    }

    fun isSavingInSharedPreferences() : Boolean {
        return didSaveDataInSharedPreferences
    }

    fun saveTodoDataInSession(context: Context) {
        TodoDataSharedPreferencesService.instance.saveTodoDataToSharedPreferences(context, todoData)
    }

    fun removeTodoData(context: Context) {
        todoData = listOf()
        TodoDataSharedPreferencesService.instance.removeAllTodos(context)
        if (!didSaveDataInSharedPreferences) TodoDatabaseService.instance.removeAllTodos(context)
    }

    fun removeTodoItem(context: Context, todoItemToRemove : TodoData) {
        todoData = todoData.toMutableList().let {
            it.remove(todoItemToRemove)
            it.toList()
        }
        when(didSaveDataInSharedPreferences) {
            true -> TodoDataSharedPreferencesService.instance.saveTodoDataToSharedPreferences(context, todoData)
            false -> TodoDatabaseService.instance.updateTodoDataInDB(context, todoData)
        }
    }

}
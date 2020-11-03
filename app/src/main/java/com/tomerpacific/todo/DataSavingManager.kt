package com.tomerpacific.todo

import android.content.Context
import com.tomerpacific.todo.adapters.TodoListAdapter
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.services.TodoDataSharedPreferencesService
import com.tomerpacific.todo.services.TodoDatabaseService

object DataSavingManager {

    private var shouldSaveInSharedPreferences : Boolean? = null

    fun decideOnUserDataSavingFlow(context: Context) {

        if (shouldSaveInSharedPreferences != null) {
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

        shouldSaveInSharedPreferences =
            when(shouldSaveDataInSharedPreferences) {
                TodoConstants.SAVE_DATA_ON_DEVICE -> true
                else -> false
        }
    }

    fun isSavingInSharedPreferences() : Boolean {
        return (shouldSaveInSharedPreferences != null && shouldSaveInSharedPreferences == true)
    }

    fun fetchTodoDataFromSavedLocation(context: Context, todoAdapter : TodoListAdapter) {
        when(shouldSaveInSharedPreferences) {
            true -> TodoDataSharedPreferencesService.instance.getTodoDataAndSet(context, todoAdapter)
            false -> TodoDatabaseService.instance.fetchTodoDataFromDBAndSet(todoAdapter)
        }
    }

    fun fetchTodoData(context: Context) {
        when(shouldSaveInSharedPreferences) {
            true -> TodoDataSharedPreferencesService.instance.getTodoData(context)
            false -> TodoDatabaseService.instance.fetchTodoDataFromDB()
        }
    }

    fun saveTodoDataInSession(context: Context, todoData : List<TodoData>) {
        TodoDataSharedPreferencesService.instance.saveTodoDataToSharedPreferences(context, todoData)
    }

    fun getTodoDataInSession(context: Context, adapter: TodoListAdapter) {
        TodoDataSharedPreferencesService.instance.getTodoDataAndSet(context, adapter)
    }

    fun updateTodoData(context : Context, todoData : List<TodoData>) {
        when(shouldSaveInSharedPreferences) {
            true -> TodoDataSharedPreferencesService.instance.saveTodoDataToSharedPreferences(context, todoData)
            false -> TodoDatabaseService.instance.updateTodoDataInDB(context, todoData)
        }
    }

    fun removeAllTodoData(context: Context) {
        if (shouldSaveInSharedPreferences != null) {
            when (shouldSaveInSharedPreferences) {
                true -> TodoDataSharedPreferencesService.instance.removeAllTodos(context)
                false -> removeTodoDataInDBAndShared(context)
            }
        }
    }

    private fun removeTodoDataInDBAndShared(context: Context) {
        TodoDatabaseService.instance.removeAllTodos(context)
        TodoDataSharedPreferencesService.instance.removeAllTodos(context)
    }


}
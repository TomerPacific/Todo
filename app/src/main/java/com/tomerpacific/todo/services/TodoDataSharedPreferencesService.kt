package com.tomerpacific.todo.services

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomerpacific.todo.TodoConstants
import com.tomerpacific.todo.adapters.TodoListAdapter
import com.tomerpacific.todo.models.TodoData
import java.lang.reflect.Type

class TodoDataSharedPreferencesService private constructor() {

    private object HOLDER {
        val INSTANCE = TodoDataSharedPreferencesService()
    }

    companion object {
        val instance : TodoDataSharedPreferencesService by lazy { HOLDER.INSTANCE }
    }

    fun getTodoDataAndSet(context: Context, todoAdapter : TodoListAdapter) {
        val todoList : List<TodoData> = getTodoDataFromSharedPreferences(context)
        todoAdapter.setTodoData(todoList.toMutableList())
    }

    fun getTodoData(context: Context) : List<TodoData> {
        return getTodoDataFromSharedPreferences(context)
    }

    fun removeAllTodos(context: Context) {
        context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).apply {
            edit().putStringSet(TodoConstants.TODO_KEY, setOf()).apply()
        }
    }

    fun saveTodoDataToSharedPreferences(context: Context, todoData : List<String>) {
        context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).apply {
            edit().putStringSet(TodoConstants.TODO_KEY, todoData.toSet()).apply()
        }
    }

    fun didUserDecideToSaveDataInSharedPreferences(context: Context) : Boolean {
        val sharedPreferences = context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(TodoConstants.SAVE_DATA_PREFERENCE_KEY, false)
    }

    private fun getTodoDataFromSharedPreferences(context: Context): List<TodoData> {
        val sharedPreferences = context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val data = sharedPreferences.getString(TodoConstants.TODO_KEY, "")
        if (data != null) {
            val listType = object : TypeToken<List<TodoData>>() {}.type
            return Gson().fromJson<List<TodoData>>(data, listType)
        }

        return listOf()
    }
}
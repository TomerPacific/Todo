package com.tomerpacific.todo.services

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomerpacific.todo.TodoConstants
import com.tomerpacific.todo.models.TodoData

class TodoDataSharedPreferencesService private constructor() {

    private object HOLDER {
        val INSTANCE = TodoDataSharedPreferencesService()
    }

    companion object {
        val instance : TodoDataSharedPreferencesService by lazy { HOLDER.INSTANCE }
    }

    fun getTodoData(context: Context) : List<TodoData> {
        return getTodoDataFromSharedPreferences(context)
    }

    fun removeAllTodos(context: Context) {
        context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).apply {
            edit().putString(TodoConstants.TODO_KEY, "").apply()
        }
    }

    fun saveTodoDataToSharedPreferences(context: Context, todoData : List<TodoData>) {
        context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).apply {
            val json : String = Gson().toJson(todoData)
            edit().putString(TodoConstants.TODO_KEY, json).apply()
        }
    }

    fun didUserDecideToSaveDataInSharedPreferences(context: Context) : Boolean {
        val sharedPreferences = context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val res = sharedPreferences.getString(TodoConstants.SAVE_DATA_PREFERENCE_KEY, "")

        if (res.isNullOrEmpty() || res == TodoConstants.SAVE_DATA_ONLINE) return false
        return true
    }

    private fun getTodoDataFromSharedPreferences(context: Context): List<TodoData> {
        val sharedPreferences = context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val data = sharedPreferences.getString(TodoConstants.TODO_KEY, "")
        if (!data.isNullOrEmpty()) {
            val listType = object : TypeToken<List<TodoData>>() {}.type
            return Gson().fromJson<List<TodoData>>(data, listType)
        }

        return listOf()
    }
}
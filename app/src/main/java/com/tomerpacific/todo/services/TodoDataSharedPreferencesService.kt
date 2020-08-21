package com.tomerpacific.todo.services

import android.content.Context
import com.tomerpacific.todo.TodoConstants
import com.tomerpacific.todo.TodoListAdapter

class TodoDataSharedPreferencesService private constructor() {

    private object HOLDER {
        val INSTANCE = TodoDataSharedPreferencesService()
    }

    companion object {
        val instance : TodoDataSharedPreferencesService by lazy { HOLDER.INSTANCE }
    }

    fun getTodoData(context: Context, todoAdapter : TodoListAdapter) {
        val todoList : List<String> = getTodoDataFromSharedPreferences(context)
        todoAdapter.setTodoData(todoList.toMutableList())
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

    private fun getTodoDataFromSharedPreferences(context: Context): List<String> {
        return context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).let {
            it.getStringSet(TodoConstants.TODO_KEY, emptyList<String>().toSet())!!.toList()
        }
    }
}
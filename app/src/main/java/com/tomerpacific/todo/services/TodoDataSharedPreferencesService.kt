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

    fun addTodo(todoTask : String) {

    }

    fun removeTodo(todoTask: String) {

    }

    fun removeAllTodos() {

    }

    fun saveTodoDataToSharedPreferences(context: Context, todoData : List<String>) {
        val sharedPref = context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putStringSet(TodoConstants.TODO_KEY, todoData.toSet()).apply()
    }

    private fun getTodoDataFromSharedPreferences(context: Context): List<String> {
        val sharedPref = context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPref.getStringSet(TodoConstants.TODO_KEY, emptyList<String>().toSet())!!.toList()

    }
}
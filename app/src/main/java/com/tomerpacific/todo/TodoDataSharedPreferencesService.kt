package com.tomerpacific.todo

import android.content.Context

class TodoDataSharedPreferencesService private constructor() {

    private var todoData : MutableList<String> = mutableListOf<String>()

    private object HOLDER {
        val INSTANCE = TodoDataSharedPreferencesService()
    }

    companion object {
        val instance : TodoDataSharedPreferencesService by lazy { HOLDER.INSTANCE }
    }

    fun getTodoData(context: Context): List<String> {
        val todoList : List<String> = getTodoDataFromSharedPreferences(context)
        todoData = todoList.toMutableList()
        return todoList
    }

    fun addTodo(todoTask : String) {
        todoData.add(todoTask)
    }

    fun removeTodo(todoTask: String) {
        todoData.remove(todoTask)
    }

    fun removeAllTodos() {
        todoData.clear()
    }

    fun saveTodoDataToSharedPreferences(context: Context) {
        val sharedPref = context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putStringSet(TodoConstants.TODO_KEY, todoData.toSet()).apply()
    }

    private fun getTodoDataFromSharedPreferences(context: Context): List<String> {
        val sharedPref = context.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPref.getStringSet(TodoConstants.TODO_KEY, emptyList<String>().toSet())!!.toList()

    }
}
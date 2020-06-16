package com.tomerpacific.todo

import android.content.Context

class TodoDataService private constructor() {

    private var todoData : MutableList<String> = mutableListOf("")

    private object HOLDER {
        val INSTANCE = TodoDataService()
    }

    companion object {
        val instance : TodoDataService by lazy { HOLDER.INSTANCE }
    }

    fun getTodoData(context: Context): List<String> {
        return getTodoDataFromSharedPreferences(context)
    }

    fun addTodo(todoTask : String) {
        todoData.add(todoTask)
    }

    fun saveTodoDataToSharedPreferences(context: Context) {
        val sharedPref = context.getSharedPreferences("todo_list_pref", Context.MODE_PRIVATE)
        sharedPref.edit().putStringSet("todo", todoData.toSet()).apply()
    }

    private fun getTodoDataFromSharedPreferences(context: Context): List<String> {
        val sharedPref = context.getSharedPreferences("todo_list_pref", Context.MODE_PRIVATE)
        return sharedPref.getStringSet("todo", listOf("").toSet())!!.toList()

    }
}
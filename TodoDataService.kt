package com.tomerpacific.todo

class TodoDataService private constructor() {

    private var todoData : MutableList<String> = mutableListOf("")

    private object HOLDER {
        val INSTANCE = TodoDataService()
    }

    companion object {
        val instance : TodoDataService by lazy { HOLDER.INSTANCE }
    }

    fun getTodoData(): List<String> {
        return todoData
    }

    fun addTodo(todoTask : String) {
        todoData.add(todoTask)
    }
}
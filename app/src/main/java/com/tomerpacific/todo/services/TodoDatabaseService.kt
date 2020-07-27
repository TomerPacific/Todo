package com.tomerpacific.todo.services

class TodoDatabaseService private constructor() {


    private object HOLDER {
        val INSTANCE = TodoDatabaseService()
    }

    companion object {
        val instance: TodoDatabaseService by lazy { HOLDER.INSTANCE }
    }

    fun removeAllTodos() {

    }
}
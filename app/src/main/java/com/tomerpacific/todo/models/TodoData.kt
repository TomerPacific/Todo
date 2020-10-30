package com.tomerpacific.todo.models

data class TodoData(var todoItem: String) {

    override fun toString(): String {
        return todoItem
    }
}
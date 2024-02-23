package com.tomerpacific.todo.view

import com.tomerpacific.todo.TodoItem

data class TodoState(
    val todoItems: List<TodoItem> = emptyList(),
    val todoItemDescription: String = "",
    val isAddingTodo: Boolean = false,
    val todoListTitle: String = "",
)

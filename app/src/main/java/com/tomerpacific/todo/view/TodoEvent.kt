package com.tomerpacific.todo.view

sealed interface TodoEvent {
    object SaveTodo: TodoEvent
    data class SetTodo(val todoDescription: String): TodoEvent
    object ShowAddTodoDialog: TodoEvent
    object HideAddTodoDialog: TodoEvent
    object DeleterTodo: TodoEvent
}
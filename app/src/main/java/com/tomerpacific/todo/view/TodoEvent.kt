package com.tomerpacific.todo.view

import com.tomerpacific.todo.TodoItem

sealed interface TodoEvent {
    object SaveTodo: TodoEvent
    data class SetTodoDescription(val todoDescription: String): TodoEvent
    object ShowAddTodoDialog: TodoEvent
    object HideAddTodoDialog: TodoEvent
    data class DeleteTodo(val todo: TodoItem): TodoEvent
    data class SetTodoListTitle(val todoListTitle: String): TodoEvent
    object RemoveAllTodos: TodoEvent
}
package com.tomerpacific.todo.view.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.todo.view.TodoEvent
import com.tomerpacific.todo.view.TodoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit
) {

    val todoListTitleText = when (state.todoListTitle.isEmpty()) {
        true -> "Your Todo List Title"
        false -> state.todoListTitle
    }

    var shouldShowTitleDialog by remember { mutableStateOf(false) }
    val shouldShowRemoveAllTodosButton = state.todoItems.isNotEmpty()

    Scaffold(floatingActionButton = {

        FloatingActionButton(
            onClick = {
                onEvent(TodoEvent.ShowAddTodoDialog)
            },
            containerColor = Color.Black,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, "Add Todo Item")
        }

    }) { paddingValues ->
        if (shouldShowTitleDialog) {
            TodoListTitleAlertDialog(state.todoListTitle, onEvent)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = {
                            shouldShowTitleDialog = true
                    }) {
                        Text(
                            text = todoListTitleText,
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline,
                            fontSize = 25.sp
                        )
                    }
                }
            }
            if (shouldShowRemoveAllTodosButton) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = {
                            onEvent(TodoEvent.SelectAllTodos)
                        }) {
                            Icon(Icons.Default.Delete,
                                "Trash can",
                                tint = Color.Red)
                        }
                    }
                }
            }

            items(state.todoItems) { todoItem ->
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        if (it == DismissValue.DismissedToEnd) {
                            onEvent(TodoEvent.DeleteTodo(todoItem))
                            true
                        } else {
                            false
                        }
                    }, positionalThreshold = { 150.dp.toPx() }
                )
                SwipeToDismiss(
                    state = dismissState,
                    background = {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "delete"
                                )
                            }
                        }
                    },
                    dismissContent = {
                        TodoItemView(todoItem, onEvent)
                    })
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }

        if (state.isAddingTodo) {
            AddTodoItemDialog(state = state, onEvent = onEvent, onDismissRequest = {
                onEvent(TodoEvent.HideAddTodoDialog)
            }, onConfirmation = {
                onEvent(TodoEvent.SaveTodo)
            })
        }
    }
}

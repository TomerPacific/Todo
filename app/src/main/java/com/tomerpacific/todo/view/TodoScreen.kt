package com.tomerpacific.todo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

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

    val shouldShowDialog = remember { mutableStateOf(false) }

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
        if (shouldShowDialog.value) {

            val todoTitle = remember {
                mutableStateOf(state.todoListTitle)
            }

            val focusRequester = remember {
                FocusRequester()
            }

            AlertDialog(onDismissRequest = {
                shouldShowDialog.value = false
            },
            text = {
                Column() {
                    Text("Choose Your Todo List Title", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequester),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        value = todoTitle.value,
                        onValueChange = {
                            todoTitle.value = it
                        },
                        placeholder = {
                            Text("Your Todo List Title")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Black,
                        ),
                    )
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(TodoEvent.SetTodoListTitle(todoTitle.value))
                        shouldShowDialog.value = false
                    },
                    enabled = todoTitle.value.isNotEmpty()
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        shouldShowDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            })
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
                            shouldShowDialog.value = true
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
            ShowAddTodoItemDialog(state,
                onEvent,
                onDismissRequest = {
                    onEvent(TodoEvent.HideAddTodoDialog)
                },
                onConfirmation = {
                    onEvent(TodoEvent.SaveTodo)
                })
        }
    }
}

@Composable
fun ShowAddTodoItemDialog(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {

    val focusRequester = remember { FocusRequester() }
    val todoItemDescriptionError = remember {
        mutableStateOf(false)
    }


    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        modifier = Modifier.focusRequester(focusRequester),
                        value = state.todoItemDescription,
                        onValueChange = { userInput: String ->
                            onEvent(TodoEvent.SetTodoDescription(userInput))
                        },
                        label = {
                            if (state.isTodoItemADuplicate) {
                                Text("There already exists a todo item with this description")
                            } else {
                                Text("What do you want to do?")
                            }
                        },
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.Edit, "Edit Icon")
                        },
                        isError = state.isTodoItemADuplicate || todoItemDescriptionError.value
                    )
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            todoItemDescriptionError.value = state.isTodoItemADuplicate
                            onConfirmation()
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = state.todoItemDescription.isNotEmpty() && !state.isTodoItemADuplicate
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
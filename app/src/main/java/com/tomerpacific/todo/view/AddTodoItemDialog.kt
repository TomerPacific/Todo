package com.tomerpacific.todo.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AddTodoItemDialog(state: TodoState,
                      onEvent: (TodoEvent) -> Unit,
                      onDismissRequest: () -> Unit,
                      onConfirmation: () -> Unit) {
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
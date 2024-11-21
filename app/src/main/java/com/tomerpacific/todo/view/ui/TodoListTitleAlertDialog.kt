package com.tomerpacific.todo.view.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tomerpacific.todo.view.TodoEvent

@Composable
fun TodoListTitleAlertDialog(todoListTitle: String,
                             onEvent: (TodoEvent) -> Unit) {

    val todoTitle = remember {
        mutableStateOf(TextFieldValue(todoListTitle, TextRange(todoListTitle.length)))
    }

    var shouldShowTitleDialog by remember { mutableStateOf(true) }

    val focusRequester = remember {
        FocusRequester()
    }

    AlertDialog(onDismissRequest = {
        shouldShowTitleDialog = false
    },
        text = {
            Column {
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
                    onEvent(TodoEvent.SetTodoListTitle(todoTitle.value.text))
                    shouldShowTitleDialog = false
                },
                enabled = todoTitle.value.text.isNotEmpty()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    shouldShowTitleDialog = false
                }
            ) {
                Text("Cancel")
            }
        })
}
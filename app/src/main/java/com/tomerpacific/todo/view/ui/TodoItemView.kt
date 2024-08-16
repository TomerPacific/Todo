package com.tomerpacific.todo.view.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.todo.TodoItem
import com.tomerpacific.todo.view.TodoEvent

@Composable
fun TodoItemView(todoItem: TodoItem,
                 onEvent: (TodoEvent) -> Unit) {

    Card(modifier = Modifier
        .fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF90a8c9)),
            verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = Modifier
                .fillMaxHeight()
                .padding(start = 5.dp),
                text = todoItem.itemDescription,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = {
                    onEvent(TodoEvent.DeleteTodo(todoItem))
                }) {
                Icon(Icons.Default.Check,
                    "Complete Todo",
                    tint = Color.Green)
            }
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = {
                    onEvent(TodoEvent.DeleteTodo(todoItem))
            }) {
                Icon(
                    Icons.Default.Close,
                    "Delete Todo",
                   tint = Color.Red)
            }
        }
    }
}
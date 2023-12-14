package com.tomerpacific.todo.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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

@Composable
fun TodoItemView(todoItem: TodoItem,
                 viewModel: MainViewModel,
                 backgroundColor: Color) {
    Card(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()
            .background(backgroundColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(modifier = Modifier.fillMaxHeight().padding(start = 5.dp),
                text = todoItem.itemDescription,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp)
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = {
                    viewModel.removeTodoItem(todoItem)
                }) {
                Icon(Icons.Default.Check,
                    "Complete Todo",
                    tint = Color.Green)
            }
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = {
                    viewModel.removeTodoItem(todoItem)
            }) {
                Icon(
                    Icons.Default.Close,
                    "Delete Todo",
                   tint = Color.Red)
            }
        }
    }
}
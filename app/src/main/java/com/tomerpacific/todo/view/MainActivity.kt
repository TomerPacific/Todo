package com.tomerpacific.todo.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tomerpacific.todo.TodoItem

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val mainViewModel: MainViewModel = viewModel()

            var todoListTitle by remember {
                mutableStateOf("")
            }

            val shouldShowTodoDialog = remember {
                mutableStateOf(false)
            }

            var todoItemsList: List<TodoItem> by remember {
                mutableStateOf(listOf())
            }

            mainViewModel.todoItemsFlow.observe(LocalLifecycleOwner.current) { todoItems ->
                todoItemsList = todoItems.itemsList
            }

            mainViewModel.todoListPreferencesFlow.observe(LocalLifecycleOwner.current) { todoListPreferences ->
                todoListTitle = todoListPreferences.title

            }

            MaterialTheme {
                Scaffold(floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            shouldShowTodoDialog.value = true
                        },
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Filled.Add, "Floating action button.")
                    }

                }) { paddingValues ->
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)) {
                        item {
                            Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                                OutlinedTextField(
                                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                    value = todoListTitle,
                                    onValueChange = {
                                        todoListTitle = it
                                        mainViewModel.updateTodoListTitle(todoListTitle)
                                    },
                                    placeholder = {
                                      Text("Your Todo List Title")
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Black,
                                        unfocusedBorderColor = Color.Transparent,
                                    ),
                                    trailingIcon = {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Icon")
                                    }
                                )
                            }
                        }

                        items(todoItemsList) { todoItem ->
                            TodoItemView(todoItem, mainViewModel)
                            Spacer(modifier = Modifier.padding(5.dp))
                        }
                    }

                    if (shouldShowTodoDialog.value) {
                        ShowAddTodoItemDialog(onDismissRequest = {
                            shouldShowTodoDialog.value = false
                        },
                        onConfirmation = {
                            mainViewModel.addTodoItem(it)
                            shouldShowTodoDialog.value = false
                        })
                    }
                }
            }
        }
    }

    @Composable
    fun ShowAddTodoItemDialog(onDismissRequest: () -> Unit,
                              onConfirmation: (String) -> Unit) {

        val todoItemDescription = remember {
            mutableStateOf("")
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
                            value = todoItemDescription.value,
                            onValueChange = { userInput: String ->
                                if (userInput.isNotEmpty()) {
                                    todoItemDescription.value = userInput
                                }
                            },
                            label = {
                                Text("What do you want to do?")
                            },
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.Edit, "Edit Icon")
                            }
                        )
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
                            onClick = { onConfirmation(todoItemDescription.value) },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }

}
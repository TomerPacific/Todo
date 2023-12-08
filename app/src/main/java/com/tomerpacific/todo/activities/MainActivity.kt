package com.tomerpacific.todo.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val mainViewModel: MainViewModel = viewModel()

            val todoListTitle = remember {
                mutableStateOf("Your Todo List Title")
            }

            val todoItems = mainViewModel.todoItems.observeAsState()

            MaterialTheme {
                Scaffold(floatingActionButton = {
                    FloatingActionButton(
                        onClick = {  },
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Filled.Add, "Floating action button.")
                    }

                }) { paddingValues ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center) {
                            OutlinedTextField(
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                value = todoListTitle.value,
                                onValueChange = {
                                    todoListTitle.value = it
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

                        if (todoItems.value != null) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(todoItems.value!!.itemsList) { todoItem ->
                                    Text(todoItem.itemDescription)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
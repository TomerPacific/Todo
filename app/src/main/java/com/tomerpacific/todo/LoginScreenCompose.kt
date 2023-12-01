package com.tomerpacific.todo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun LoginScreenCompose() {
    val email = remember {
        mutableStateOf("")
    }

    Column() {
        Row() {
            TextField(
                value = email.value,
                onValueChange = { it: String -> email.value = it },
                label = {
                    Text("Email")
                }
            )
        }
    }
}
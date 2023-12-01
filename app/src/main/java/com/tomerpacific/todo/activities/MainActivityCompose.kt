package com.tomerpacific.todo.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import com.tomerpacific.todo.LoginScreenCompose

class MainActivityCompose: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                LoginScreenCompose()
            }
        }
    }

}
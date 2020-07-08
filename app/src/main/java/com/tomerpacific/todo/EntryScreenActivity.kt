package com.tomerpacific.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class EntryScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = this.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val isFirstLogin = sharedPref.getBoolean(TodoConstants.FIRST_LOGIN_KEY, true)
        val intent = when(isFirstLogin) {
            true -> Intent(this, LoginActivity::class.java)
            false -> Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

}
package com.tomerpacific.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class EntryScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = this.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val savingPreference = sharedPref.getString(TodoConstants.SAVE_DATA_PREFERENCE_KEY, "")
        val isFirstLogin = sharedPref.getBoolean(TodoConstants.FIRST_LOGIN_KEY, true)

        var intent : Intent?

        if (isFirstLogin) {
            intent = Intent(this, LoginActivity::class.java)
        } else {
            intent = when (savingPreference) {
                TodoConstants.SAVE_DATA_ON_DEVICE -> Intent(this, MainActivity::class.java)
                TodoConstants.SAVE_DATA_ONLINE -> Intent(this, LoginActivity::class.java)
                else -> null
            }
        }

        startActivity(intent)
        finish()
    }

}
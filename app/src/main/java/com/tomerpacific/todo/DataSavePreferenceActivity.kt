package com.tomerpacific.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class DataSavePreferenceActivity : AppCompatActivity() {

    private lateinit var switch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_save_preference)
        setSupportActionBar(toolbar)

        switch = findViewById(R.id.switch1)

        switch.setOnCheckedChangeListener {_, isChecked ->
            switch.text = if (isChecked) TodoConstants.SAVE_DATA_ONLINE else TodoConstants.SAVE_DATA_ON_DEVICE
        }

    }

    fun moveToMain(view : View) {

        val sharedPref = this.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(TodoConstants.FIRST_LOGIN_KEY, false).apply()
        sharedPref.edit().putString(TodoConstants.SAVE_DATA_PREFERENCE_KEY, switch.text.toString()).apply()

        val mainIntent : Intent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
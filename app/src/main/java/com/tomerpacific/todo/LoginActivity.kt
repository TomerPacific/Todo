package com.tomerpacific.todo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    private lateinit var switch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        switch = findViewById(R.id.switch1)

        switch.setOnCheckedChangeListener {_, isChecked ->
            switch.text = if (isChecked) "Online" else "On Device"
        }

    }

    fun moveToMain(view : View) {
        val mainIntent : Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("savePreference", switch.text.toString())
        }

        startActivity(mainIntent)
        finish()
    }
}
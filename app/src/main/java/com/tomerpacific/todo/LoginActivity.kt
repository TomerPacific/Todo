package com.tomerpacific.todo

import android.os.Bundle
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
}
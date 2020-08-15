package com.tomerpacific.todo.activities

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tomerpacific.todo.R
import com.tomerpacific.todo.TodoConstants

class TodoFormActivity : AppCompatActivity() {

    private lateinit var doneButton: Button
    private lateinit var todoChoreEditText: EditText
    private var choreText : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_form)

        doneButton = findViewById<Button>(R.id.todo_item_button).apply {
            setOnClickListener { view ->
                when(choreText.isBlank()) {
                    true ->  Toast.makeText(
                        this@TodoFormActivity,
                        TodoConstants.USER_NOTIFICATION_MISSING_TODO, Toast.LENGTH_SHORT
                    ).show()
                    false -> let {
                        val intent: Intent = Intent(this@TodoFormActivity, MainActivity::class.java)
                        intent.putExtra(TodoConstants.TODO_ACTION_NEW_TODO_ITEM, choreText)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        todoChoreEditText = findViewById(R.id.todo_edit_text)

        setupListeners()
    }

    private fun setupListeners() {

        todoChoreEditText.setOnEditorActionListener {view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent == null || keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                choreText = view.text.toString()
                true
            }
            false
        }

        todoChoreEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                choreText = todoChoreEditText.text.toString()
            }
        }

    }
}
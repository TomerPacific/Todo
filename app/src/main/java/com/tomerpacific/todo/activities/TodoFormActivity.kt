package com.tomerpacific.todo.activities

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.tomerpacific.todo.R
import com.tomerpacific.todo.TodoConstants
import com.tomerpacific.todo.models.TodoData

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
                        val todoItem : TodoData = TodoData(choreText)
                        val intent: Intent = Intent(this@TodoFormActivity, MainActivity::class.java)
                        intent.putExtra(TodoConstants.TODO_ACTION_NEW_TODO_ITEM, Gson().toJson(todoItem))
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        todoChoreEditText = findViewById<EditText>(R.id.todo_edit_text).apply {
            setOnEditorActionListener {view, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent == null || keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    choreText = view.text.toString()
                    true
                }
                false
            }

            setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    choreText = todoChoreEditText.text.toString()
                }
            }
        }
    }

}
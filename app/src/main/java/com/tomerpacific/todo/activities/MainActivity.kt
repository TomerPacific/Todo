package com.tomerpacific.todo.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tomerpacific.todo.*

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var list : ListView
    private lateinit var title : EditText
    private lateinit var clearButton : Button
    private var signOutButton : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        DataSavingManager.decideOnUserDataSavingFlow(this)

        clearButton = findViewById(R.id.clearBtn)

        title = findViewById(R.id.title)

        signOutButton = findViewById(R.id.Logout)

        setupListeners()

        list = findViewById(R.id.todo_list)
        list.adapter = TodoListAdapter(this, clearButton)

        val adapter = list.adapter as TodoListAdapter
        DataSavingManager.getTodoDataInSession(this, adapter)

        setClearButtonStatus(list.adapter.count != 0)

        setSignoutButtonStatus()

        val todoItemToBeAdd : String? = intent.getStringExtra("NEW_TODO_ITEM")
        if (todoItemToBeAdd != null) {
            adapter.addTodoItem(todoItemToBeAdd)
            DataSavingManager.updateTodoData(this, adapter.getTodoData())
        } else {
            DataSavingManager.fetchTodoDataFromSavedLocation(this, list.adapter as TodoListAdapter)
        }
    }

    private fun setupListeners() {

        title.setOnEditorActionListener {view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                keyEvent == null ||
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                true
            }
            false
        }

        val handler = Handler()
        val runnable = Runnable {
            Toast.makeText(this, "You have changed the title to " + title.text.toString(), Toast.LENGTH_SHORT).show()
        }
        title.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 5000)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        fab.setOnClickListener { view ->
            val intent : Intent = Intent(this, TodoFormActivity::class.java)
            startActivity(intent)
            finish()
        }

        signOutButton?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent : Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setSignoutButtonStatus() {
        if (!DataSavingManager.isSavingInSharedPreferences()) {
            signOutButton?.visibility = View.VISIBLE
        }
    }

    private fun setClearButtonStatus(status: Boolean) {
        clearButton.isClickable = status
        clearButton.background.colorFilter = if(!clearButton.isClickable)
            PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY) else PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun removeAll(view: View) {
        val adapter = list.adapter as TodoListAdapter
        adapter.removeAllTodos()
        setClearButtonStatus(false)
        DataSavingManager.removeAllTodoData(this)
    }

    override fun onPause() {
        super.onPause()
        val adapter = list.adapter as TodoListAdapter
        val todoData : List<String> = adapter.getTodoData()
        DataSavingManager.saveTodoDataInSession(this, todoData)
    }

    fun shareWithWhatsApp(view: View) {
        val adapter = list.adapter as TodoListAdapter

        val whatsappIntent : Intent = Intent()
        whatsappIntent.action = Intent.ACTION_SEND
        whatsappIntent.`package`= "com.whatsapp"
        val todoList : String = title.text.toString() + " " + adapter.getTodoData().joinToString(prefix = "*", separator = "*")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, todoList)
        whatsappIntent.type = "text/plain"

        if (whatsappIntent.resolveActivity(packageManager) == null) {
            Toast.makeText(this, "Whatsapp has not been found on the device", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(whatsappIntent)
        }

    }
}

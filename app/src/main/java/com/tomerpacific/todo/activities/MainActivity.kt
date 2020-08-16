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

    private lateinit var todoListView : ListView
    private lateinit var todoListTitle : EditText
    private lateinit var clearButton : Button
    private var signOutButton : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        DataSavingManager.decideOnUserDataSavingFlow(this)

        clearButton = findViewById(R.id.clearBtn)

        todoListTitle = findViewById(R.id.title)

        signOutButton = findViewById(R.id.Logout)

        setupListeners()

        setSignoutButtonStatus()

        todoListView = findViewById<ListView>(R.id.todo_list).apply {
            val listAdapter = TodoListAdapter(this@MainActivity, this@MainActivity::setClearButtonStatus)
            this.adapter = listAdapter
            DataSavingManager.getTodoDataInSession(this@MainActivity, this.adapter as TodoListAdapter)

            val todoItemToBeAdd : String? = intent.getStringExtra(TodoConstants.TODO_ACTION_NEW_TODO_ITEM)
            todoItemToBeAdd.let {
                if (it != null) {
                    listAdapter.addTodoItem(it)
                    DataSavingManager.updateTodoData(this@MainActivity, listAdapter.getTodoData())
                } else {
                    DataSavingManager.fetchTodoDataFromSavedLocation(this@MainActivity, listAdapter)
                }
            }
        }
    }

    private fun setupListeners() {

        todoListTitle.setOnEditorActionListener { view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                keyEvent == null ||
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                true
            }
            false
        }

        val handler = Handler()
        val runnable = Runnable {
            Toast.makeText(this, "You have changed the todoListTitle to " + todoListTitle.text.toString(), Toast.LENGTH_SHORT).show()
        }
        todoListTitle.addTextChangedListener (object : TextWatcher {
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
        val adapter = todoListView.adapter as TodoListAdapter
        adapter.removeAllTodos()
        DataSavingManager.removeAllTodoData(this)
    }

    private fun setClearButtonStatus(status: Boolean) {

        clearButton.apply {
            isClickable = status
            background.colorFilter = when(isClickable) {
                true -> PorterDuffColorFilter(
                    Color.GREEN, PorterDuff.Mode.MULTIPLY)
                false -> PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val adapter = todoListView.adapter as TodoListAdapter
        val todoData : List<String> = adapter.getTodoData()
        DataSavingManager.saveTodoDataInSession(this, todoData)
    }

    fun shareWithWhatsApp(view: View) {
        val adapter = todoListView.adapter as TodoListAdapter
        val todoList : String = todoListTitle.text.toString() + " " + adapter.getTodoData().joinToString(prefix = "*", separator = "*")

        val whatsappIntent : Intent = Intent().apply {
            action = Intent.ACTION_SEND
            `package`= TodoConstants.WHATSAPP_PACKAGE
            putExtra(Intent.EXTRA_TEXT, todoList)
            type = "text/plain"
        }

        when(whatsappIntent.resolveActivity(packageManager)) {
            null -> Toast.makeText(this, "Whatsapp has not been found on the device", Toast.LENGTH_SHORT).show()
            else -> startActivity(whatsappIntent)
        }
    }
}

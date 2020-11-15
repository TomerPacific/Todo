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
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomerpacific.todo.*
import com.tomerpacific.todo.adapters.TodoListAdapter
import com.tomerpacific.todo.models.TodoData
import com.tomerpacific.todo.repositories.TodoRepository
import com.tomerpacific.todo.viewmodels.MainActivityViewModel

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var todoListView : ListView
    private lateinit var todoListAdapter : TodoListAdapter
    private lateinit var todoListTitle : EditText
    private lateinit var clearButton : Button
    private var signOutButton : Button? = null
    private lateinit var mMainActivityViewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        TodoRepository.decideOnUserDataSavingFlow(this)

        clearButton = findViewById(R.id.clearBtn)

        todoListTitle = findViewById(R.id.title)

        signOutButton = findViewById(R.id.Logout)

        setupListeners()

        setSignoutButtonStatus()

        mMainActivityViewModel = ViewModelProvider(this, ViewModelFactory(application)).get(MainActivityViewModel::class.java)

        mMainActivityViewModel.getTodoData().observe(this, Observer { it ->
            todoListAdapter.submitList(it)
        })

        initListView()

        val todoItemToBeAddedJson: String? = intent.getStringExtra(TodoConstants.TODO_ACTION_NEW_TODO_ITEM)
        if (!todoItemToBeAddedJson.isNullOrEmpty()) {
            val listType = object : TypeToken<TodoData>() {}.type
            val todoItemToBeAdded : TodoData = Gson().fromJson<TodoData>(todoItemToBeAddedJson, listType)
            mMainActivityViewModel.addTodo(todoItemToBeAdded)
        }

    }

    private fun initListView() {

        todoListView = findViewById<ListView>(R.id.todo_list).apply {
            todoListAdapter =
                TodoListAdapter(
                    this@MainActivity,
                    mMainActivityViewModel
                )
            adapter = todoListAdapter
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
            val builder = AlertDialog.Builder(this)
            val userInputEditText : EditText = EditText(this).apply {
                this.maxLines = 1
                this.isSingleLine = true
                this.setLines(1)
            }

            builder.setView(userInputEditText)
            builder.setTitle("Add A New Task")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Yes"){ dialogInterface, which ->
                if (!userInputEditText.text.isNullOrEmpty()) {
                    mMainActivityViewModel.addTodo(TodoData(userInputEditText.text.toString()))
                }
            }

            builder.setNegativeButton("No"){ dialogInterface, which ->

            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

            userInputEditText.apply {
                addTextChangedListener (object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        if (!p0.isNullOrEmpty()) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }
                })
            }

        }

        signOutButton?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent : Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setSignoutButtonStatus() {
        if (!TodoRepository.isSavingInSharedPreferences()) {
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
        mMainActivityViewModel.removeAllTodoData(this)
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
        mMainActivityViewModel.saveTodoData(this)
    }

    fun shareWithWhatsApp(view: View) {
        val todoList : String = todoListTitle.text.toString() + " " + mMainActivityViewModel.getTodoData().value.orEmpty().joinToString(prefix = "*", separator = "*")

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

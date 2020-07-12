package com.tomerpacific.todo

import android.content.Context
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
import com.google.firebase.auth.FirebaseUser

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var list : ListView
    private lateinit var title : EditText
    private lateinit var clearButton : Button
    private var shouldSaveDataInSharedPreferences : String? = null
    private var isInSharedPreferencesFlow : Boolean = false
    private var user : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        decideOnUserDataSavingFlow()

        clearButton = findViewById(R.id.clearBtn)

        title = findViewById(R.id.title)

        setupListeners()

        list = findViewById(R.id.todo_list)
        list.adapter = TodoListAdapter(this, clearButton)

        setClearButtonStatus(list.adapter.count != 0)
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
    }

    fun shareWithWhatsApp(view: View) {

        val todos = TodoDataService.instance.getTodoData(this)

        val whatsappIntent : Intent = Intent()
        whatsappIntent.action = Intent.ACTION_SEND
        whatsappIntent.`package`= "com.whatsapp"
        val todoList : String = title.text.toString() + " " + todos.joinToString(prefix = "*", separator = "*")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, todoList)
        whatsappIntent.type = "text/plain"

        if (whatsappIntent.resolveActivity(packageManager) == null) {
            Toast.makeText(this, "Whatsapp has not been found on the device", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(whatsappIntent)
        }

    }

    fun logoutUser(view : View) {
        FirebaseAuth.getInstance().signOut()
        val signOutButton : Button = view as Button
        signOutButton.visibility = View.INVISIBLE
    }

    private fun decideOnUserDataSavingFlow() {
        val sharedPref = this.getSharedPreferences(TodoConstants.TODO_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        shouldSaveDataInSharedPreferences = sharedPref.getString(TodoConstants.SAVE_DATA_PREFERENCE_KEY, TodoConstants.SAVE_DATA_ON_DEVICE)

        if (shouldSaveDataInSharedPreferences.isNullOrEmpty() || shouldSaveDataInSharedPreferences.equals(TodoConstants.SAVE_DATA_ON_DEVICE)) {
            isInSharedPreferencesFlow = true
        } else {
            user = FirebaseAuth.getInstance().currentUser
            val signOutButton : Button = findViewById(R.id.Logout)
            signOutButton.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        if (isInSharedPreferencesFlow) {
            TodoDataService.instance.saveTodoDataToSharedPreferences(this)
        }

    }
}

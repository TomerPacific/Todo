package com.tomerpacific.todo

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ListView

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var list : ListView
    private lateinit var title : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        title = findViewById(R.id.title)

        title.setOnEditorActionListener {view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                keyEvent == null ||
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                true
            }
            false
        }

        list = findViewById(R.id.todo_list)
        list.adapter = TodoListAdapter(this)

        fab.setOnClickListener { view ->
            val intent : Intent = Intent(this, TodoFormActivity::class.java)
            startActivity(intent)
            finish()
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
        val adapter = list.adapter as TodoListAdapter
        adapter.removeAllTodos()
    }

    override fun onStop() {
        super.onStop()
        TodoDataService.instance.saveTodoDataToSharedPreferences(this)
    }
}

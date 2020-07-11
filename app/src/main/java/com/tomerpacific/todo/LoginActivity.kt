package com.tomerpacific.todo

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var userEmail : String = ""
    private var userPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        val userEmailEditText : EditText = findViewById(R.id.email_edit_text)
        val passwordEditText : EditText = findViewById(R.id.password_edit_text)

        userEmailEditText.setOnEditorActionListener {view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent == null ||
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                val edit = view as EditText
                userEmail = edit.text.toString()
                true
            }
            false
        }

        passwordEditText.setOnEditorActionListener {view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent == null ||
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                val edit = view as EditText
                userPassword = edit.text.toString()
                true
            }
            false
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun loginUser(view : View) {
        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent : Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                }
            }
    }

    fun signupUser(view: View) {

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            return
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent : Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {

                }
            }
    }
}
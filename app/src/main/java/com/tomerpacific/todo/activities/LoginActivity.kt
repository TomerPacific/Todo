package com.tomerpacific.todo.activities

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.tomerpacific.todo.R

class LoginActivity : AppCompatActivity() {

    private var userEmail : String = ""
    private var userPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<EditText>(R.id.email_edit_text).apply {
            setOnEditorActionListener {_, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    keyEvent == null ||
                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    userEmail = text.toString()
                }
                false
            }
        }

        findViewById<EditText>(R.id.password_edit_text).apply {
            setOnEditorActionListener {_, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    keyEvent == null ||
                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    userPassword = text.toString()
                }
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().currentUser?.let {
            Intent(this@LoginActivity, MainActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    fun loginUser(view : View) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateFirebaseUserDisplayName()
                } else {
                    Toast.makeText(this, "An error has occurred during login. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signupUser(view: View) {

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please make sure to fill in your email and password", Toast.LENGTH_SHORT).show()
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateFirebaseUserDisplayName()
                } else {
                    Toast.makeText(this, "An error has occurred during signup. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateFirebaseUserDisplayName() {
        val intent : Intent = Intent(this, MainActivity::class.java)
        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val profileUpdates : UserProfileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(userEmail).build()
        user?.updateProfile(profileUpdates)?.addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                startActivity(intent)
                finish()
            }
        })
    }

}
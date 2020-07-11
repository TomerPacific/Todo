package com.tomerpacific.todo

import android.content.Intent
import android.os.Bundle
import android.view.View
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
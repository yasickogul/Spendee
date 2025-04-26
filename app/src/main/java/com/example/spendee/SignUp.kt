package com.example.spendee

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cashora.R

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val emailInput = findViewById<EditText>(R.id.email_input)
        val nameInput = findViewById<EditText>(R.id.name_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val registerButton = findViewById<Button>(R.id.login_button)
        val signInLink = findViewById<TextView>(R.id.sign_up_link)

        val sharedPref = getSharedPreferences("UserCredentials", MODE_PRIVATE)
        val isRegistered = sharedPref.getBoolean("isRegistered", false)

        // If already registered, skip SignUp
        if (isRegistered) {
            Toast.makeText(this, "Account already exists. Please sign in.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val username = nameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            with(sharedPref.edit()) {
                putString("email", email)
                putString("username", username)
                putString("password", password)
                putBoolean("isRegistered", true)
                apply()
            }

            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }

        signInLink.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }
    }
}

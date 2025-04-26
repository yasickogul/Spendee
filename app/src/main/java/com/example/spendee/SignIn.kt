package com.example.spendee

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cashora.R
import com.example.spendee.ui.MainActivity

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.login_button)
        val signUpLink = findViewById<TextView>(R.id.sign_up_link)

        val sharedPref = getSharedPreferences("UserCredentials", MODE_PRIVATE)
        val storedEmail = sharedPref.getString("email", "")
        val storedPassword = sharedPref.getString("password", "")

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email == storedEmail && password == storedPassword) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        signUpLink.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }
}

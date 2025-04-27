package com.example.spendee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashora.R

class Logo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make it full screen (no system bars)
        enableEdgeToEdge()

        // Set the content view to logo layout
        setContentView(R.layout.activity_logo)

        // Apply window insets to prevent content from being hidden under system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the logo view by the correct ID (imageView)
        val logoView = findViewById<View>(R.id.imageView)

        // Load the fade-in and fade-out animation
        val fadeInOut = AnimationUtils.loadAnimation(this, R.anim.transition)

        // Apply the animation to the logo view
        logoView.startAnimation(fadeInOut)

        // After 3 seconds (to allow the animation to complete), navigate to Onboard1
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Onboard1::class.java))
            finish()  // Close the logo activity to prevent going back to it
        }, 3000)  // 3000 milliseconds = 3 seconds to cover the full animation
    }
}

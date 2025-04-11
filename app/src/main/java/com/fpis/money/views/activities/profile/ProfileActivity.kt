package com.fpis.money.views.activities.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fpis.money.R
import com.fpis.money.R.layout.activity_profile
import com.fpis.money.utils.preferences.SharedPreferencesManager
import com.fpis.money.views.activities.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_profile)

        sharedPrefs = SharedPreferencesManager(this)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        val usernameText = findViewById<TextView>(R.id.usernameText)
        val emailText = findViewById<TextView>(R.id.emailText)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        user?.let {
            usernameText.text = it.displayName ?: "No username"
            emailText.text = it.email ?: "No email"
        }

        changePasswordButton.setOnClickListener {
            val email = user?.email
            if (email != null) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Reset email sent to $email", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            sharedPrefs.setUserLoggedIn(false)

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
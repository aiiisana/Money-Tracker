package com.fpis.money.views.activities.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fpis.money.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var sendEmailButton: Button
    private lateinit var backToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailInput = findViewById(R.id.emailInput)
        sendEmailButton = findViewById(R.id.btnSendResetEmail)
        backToLogin = findViewById(R.id.backToLogin)

        sendEmailButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                showToast("Please enter your email")
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Reset email sent. Check your inbox.")
                            finish()
                        } else {
                            showToast(task.exception?.message ?: "Something went wrong")
                        }
                    }
            }
        }

        backToLogin.setOnClickListener {
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_SHORT).show()
    }
}
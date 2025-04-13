package com.fpis.money.views.activities.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fpis.money.R
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast

class RegisterActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginRedirect: TextView
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        // Initialize views
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        signUpButton = findViewById(R.id.signUpButton)
        loginRedirect = findViewById(R.id.loginRedirect)

        signUpButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (validateInputs(name, email, password)) {
                viewModel.register(name, email, password).observe(this) { result ->
                    if (result) {
                        val intent = Intent(this, LoginActivity::class.java)
                        showCustomToast(this, "Registration successful!", ToastType.SUCCESS)
                        startActivity(intent)
                        finish()
                    } else {
                        showCustomToast(this, "Registration failed!", ToastType.SUCCESS)
                    }
                }
            }
        }

        // Add click listener for login redirect
        loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            nameInput.error = "Name cannot be empty"
            return false
        }

        if (email.isEmpty()) {
            emailInput.error = "Email cannot be empty"
            return false
        }

        if (password.isEmpty()) {
            passwordInput.error = "Password cannot be empty"
            return false
        }

        if (password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            return false
        }

        return true
    }
}
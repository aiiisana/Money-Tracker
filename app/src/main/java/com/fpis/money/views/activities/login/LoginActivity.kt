package com.fpis.money.views.activities.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.fpis.money.R
import com.fpis.money.views.activities.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectText: TextView

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginUsername = findViewById(R.id.emailInput)
        loginPassword = findViewById(R.id.passwordInput)
        signupRedirectText = findViewById(R.id.createAccount)
        loginButton = findViewById(R.id.signInButton)

        loginButton.setOnClickListener {
            if (!validateUsername() || !validatePassword()) {
                Toast.makeText(this, "Invalid format", Toast.LENGTH_LONG).show()
            } else {
                val username = loginUsername.text.toString().trim()
                val password = loginPassword.text.toString().trim()
                loginViewModel.checkUser(username, password)
            }
        }

        signupRedirectText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginViewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is LoginResult.Success -> {
                    val user = result.user
                    // Выводим данные пользователя в логи
                    println("User logged in: Username: ${user.username}, Email: ${user.email}")

                    // Переходим на главный экран
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun validateUsername(): Boolean {
        val value = loginUsername.text.toString()
        return if (value.isEmpty()) {
            loginUsername.error = "Username cannot be empty"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val value = loginPassword.text.toString()
        return if (value.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }
}
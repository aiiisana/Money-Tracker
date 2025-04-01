package com.fpis.money.views.activities.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fpis.money.models.User
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    fun checkUser(userEmail: String, userPassword: String) {
        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val loggedInUser = User(user.displayName ?: "", user.email ?: "", "")
                        _loginResult.postValue(LoginResult.Success(loggedInUser))
                    } else {
                        _loginResult.postValue(LoginResult.Error("User not found"))
                    }
                } else {
                    _loginResult.postValue(LoginResult.Error(task.exception?.message ?: "Login failed"))
                }
            }
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
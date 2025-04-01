package com.fpis.money.views.activities.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    fun checkUser(userEmail: String, userPassword: String) {
        // Простая имитация проверки пользователя без Firebase
        if (userEmail == "user@user.com" && userPassword == "pass") {
            // Создаём пользователя и выводим его данные в логах
            val user = User("Test User", "user@user.com", "testUser", "pass")
            Log.d("Login", "User found: $user")
            _loginResult.postValue(LoginResult.Success(user))
        } else {
            _loginResult.postValue(LoginResult.Error("Invalid Credentials"))
        }
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

data class User(val name: String, val email: String, val username: String, val password: String)
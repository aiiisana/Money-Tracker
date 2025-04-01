package com.fpis.money.views.activities.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class RegisterViewModel : ViewModel() {
    fun register(name: String, email: String, password: String) = liveData(Dispatchers.IO) {
        try {
            //Firebase
            emit(true) // Симуляция успешной регистрации
        } catch (e: Exception) {
            emit(false)
        }
    }
}

package com.fpis.money.views.activities.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(name: String, email: String, password: String) = liveData(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                user.updateProfile(userProfileChangeRequest { displayName = name }).await()
                emit(true)
            } ?: emit(false)
        } catch (e: Exception) {
            emit(false)
        }
    }
}
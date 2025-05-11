package com.fpis.money.views.activities.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.fpis.money.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun register(name: String, email: String, password: String) = liveData(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                user.updateProfile(userProfileChangeRequest { displayName = name }).await()

                val newUser = User(
                    id = user.uid,
                    username = name,
                    email = email,
                    role = "user"
                )

                firestore.collection("users").document(user.uid).set(newUser).await()

                emit(true)
            } ?: emit(false)
        } catch (e: Exception) {
            emit(false)
        }
    }
}
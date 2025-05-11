package com.fpis.money.utils

import com.fpis.money.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AuthHelper {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun isAdmin(): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.getString("role") == "admin"
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCurrentUser(): User? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(User::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }
}
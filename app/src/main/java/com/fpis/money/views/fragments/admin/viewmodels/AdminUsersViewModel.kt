package com.fpis.money.views.fragments.admin.viewmodels

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpis.money.models.User
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminUsersViewModel : ViewModel() {
    private var snapshotListener: ListenerRegistration? = null
    private val firestore = FirebaseFirestore.getInstance()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        _loading.value = true
        snapshotListener?.remove()

        snapshotListener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                _loading.value = false
                if (error != null) {
                    Log.e("AdminUsersVM", "Error loading users", error)
                    return@addSnapshotListener
                }

                val userList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<User>()?.copy(id = doc.id)
                } ?: emptyList()

                _users.value = userList
            }
    }


    fun searchUsers(query: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val snapshot = if (query.isBlank()) {
                    firestore.collection("users").get().await()
                } else {
                    firestore.collection("users")
                        .whereGreaterThanOrEqualTo("email", query)
                        .whereLessThanOrEqualTo("email", query + "\uf8ff")
                        .get()
                        .await()
                }

                _users.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<User>()?.copy(id = doc.id)
                }
            } catch (_: Exception) {
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateUserRole(userId: String, isAdmin: Boolean) {
        viewModelScope.launch {
            try {
                firestore.collection("users").document(userId)
                    .update("role", if (isAdmin) "admin" else "user")
                    .await()
            } catch (_: Exception) {
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("users").document(userId).delete().await()
            } catch (_: Exception) {
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                firestore.collection("users").document(user.id).set(user).await()
            } catch (_: Exception) {
            }
        }
    }

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun addUser(user: User, password: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val authResult = Firebase.auth.createUserWithEmailAndPassword(user.email, password).await()

                val newUser = user.copy(id = authResult.user?.uid ?: "")
                firestore.collection("users").document(newUser.id).set(newUser).await()

                _toastMessage.emit("User added successfully")
            } catch (e: Exception) {
                Log.e("AdminUsers","Failed to add user: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    override fun onCleared() {
        snapshotListener?.remove()
        super.onCleared()
    }
}
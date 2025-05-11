package com.fpis.money.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class User(
    @DocumentId val id: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "user",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long? = null,
    val isActive: Boolean = true
) {
    constructor() : this("", "", "", "user", 0, null, true)

    @get:Exclude
    val admin: Boolean
        get() = role == "admin"
}
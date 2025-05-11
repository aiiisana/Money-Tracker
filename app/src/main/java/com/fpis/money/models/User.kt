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
    val isAdmin: Boolean
        get() = role.equals("admin", ignoreCase = true)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}
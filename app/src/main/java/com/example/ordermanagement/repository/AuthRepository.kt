package com.example.ordermanagement.repository

import com.example.ordermanagement.firebase.FirebaseAuthManager
import com.example.ordermanagement.firebase.FirebaseUserRoleManager

class AuthRepository {
    suspend fun register(email: String, password: String, name: String, role: String = "user") =
        FirebaseAuthManager.registerUser(email, password, name, role)

    suspend fun login(email: String, password: String) =
        FirebaseAuthManager.loginUser(email, password)

    suspend fun getRole(): String? {
        val uid = FirebaseAuthManager.currentUserId() ?: return null
        return FirebaseUserRoleManager.getUserRole(uid)
    }

    fun logout() = FirebaseAuthManager.logout()
}

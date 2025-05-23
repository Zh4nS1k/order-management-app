package com.example.ordermanagement.model

data class AuditLog(
    val userEmail: String = "",
    val action: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

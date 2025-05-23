package com.example.ordermanagement.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val itemName: String = "",
    val status: String = "Pending",
    val timestamp: Long = System.currentTimeMillis()
)

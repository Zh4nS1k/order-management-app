package com.example.ordermanagement.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ordermanagement.model.AuditLog
import com.example.ordermanagement.model.Order
import com.example.ordermanagement.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())
    val allOrders = _allOrders.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs = _auditLogs.asStateFlow()

    fun loadAllUsers() {
        db.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error fetching users: ${error.message}")
                return@addSnapshotListener
            }

            _users.value = snapshot?.documents?.mapNotNull {
                try {
                    it.toObject(User::class.java)
                } catch (e: Exception) {
                    println("Failed to parse user: ${e.message}")
                    null
                }
            } ?: emptyList()
        }
    }

    fun loadAllOrders() {
        db.collection("orders").addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error fetching orders: ${error.message}")
                return@addSnapshotListener
            }

            _allOrders.value = snapshot?.documents?.mapNotNull {
                try {
                    val order = it.toObject(Order::class.java)
                    order?.copy(id = it.id)
                } catch (e: Exception) {
                    println("Failed to parse order: ${e.message}")
                    null
                }
            } ?: emptyList()
        }
    }

    fun updateOrderStatus(order: Order) {
        if (order.id.isBlank()) return
        val newStatus = when (order.status) {
            "Pending" -> "Processing"
            "Processing" -> "Delivered"
            else -> "Pending"
        }
        db.collection("orders").document(order.id).update("status", newStatus)
            .addOnSuccessListener {
                logAction("Updated order status: ${order.itemName} → $newStatus")
            }
            .addOnFailureListener {
                println("Failed to update order status: ${it.message}")
            }
    }

    fun editUser(user: User) {
        if (user.uid.isBlank()) return
        db.collection("users").document(user.uid).set(user)
            .addOnSuccessListener {
                logAction("Edited user: ${user.email}")
            }
            .addOnFailureListener {
                println("Failed to edit user: ${it.message}")
            }
    }

    fun deleteUser(user: User) {
        if (user.uid.isBlank()) return
        db.collection("users").document(user.uid).delete()
            .addOnSuccessListener {
                logAction("Deleted user: ${user.email}")
            }
            .addOnFailureListener {
                println("Failed to delete user: ${it.message}")
            }
    }

    fun loadAuditLogs() {
        db.collection("audit_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching audit logs: ${error.message}")
                    return@addSnapshotListener
                }

                _auditLogs.value = snapshot?.documents?.mapNotNull {
                    try {
                        it.toObject(AuditLog::class.java)
                    } catch (e: Exception) {
                        println("Failed to parse audit log: ${e.message}")
                        null
                    }
                } ?: emptyList()
            }
    }

    private fun logAction(action: String) {
        val log = AuditLog(
            userEmail = auth.currentUser?.email ?: "Unknown",
            action = action,
            timestamp = System.currentTimeMillis()
        )
        db.collection("audit_logs").add(log)
    }

    fun logout() {
        auth.signOut()
    }
}

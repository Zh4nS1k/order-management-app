package com.example.ordermanagement.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ordermanagement.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    fun loadUserOrders() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("orders")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching orders: ${error.message}")
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull {
                    try {
                        it.toObject(Order::class.java)?.copy(id = it.id)
                    } catch (e: Exception) {
                        println("Error parsing order: ${e.message}")
                        null
                    }
                }
                _orders.value = list ?: emptyList()
            }
    }

    fun createOrder(itemName: String) {
        val uid = auth.currentUser?.uid ?: return
        val newOrder = Order(userId = uid, itemName = itemName)
        db.collection("orders")
            .add(newOrder)
            .addOnSuccessListener {
                logAction("Created order: ${newOrder.itemName}")
            }
            .addOnFailureListener {
                println("Failed to create order: ${it.message}")
            }
    }

    fun editOrder(order: Order) {
        if (order.id.isBlank()) return
        db.collection("orders").document(order.id).set(order)
            .addOnSuccessListener {
                logAction("Edited order: ${order.itemName}")
            }
            .addOnFailureListener {
                println("Failed to edit order: ${it.message}")
            }
    }

    fun deleteOrder(order: Order) {
        if (order.id.isBlank()) return
        db.collection("orders").document(order.id).delete()
            .addOnSuccessListener {
                logAction("Deleted order: ${order.itemName}")
            }
            .addOnFailureListener {
                println("Failed to delete order: ${it.message}")
            }
    }

    private fun logAction(action: String) {
        val log = mapOf(
            "userEmail" to auth.currentUser?.email,
            "action" to action,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("audit_logs").add(log)
    }
    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

}

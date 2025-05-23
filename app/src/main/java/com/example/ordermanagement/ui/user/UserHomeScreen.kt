package com.example.ordermanagement.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.ordermanagement.model.Order
import com.example.ordermanagement.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(viewModel: UserViewModel) {
    val orders by viewModel.orders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Pair<Boolean, Order?>>(false to null) }
    var showDeleteDialog by remember { mutableStateOf<Order?>(null) }
    var itemName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadUserOrders()
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                itemName = ""
            },
            title = { Text("Add New Order", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    singleLine = true,
                    isError = itemName.isBlank()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (itemName.isNotBlank()) {
                            viewModel.createOrder(itemName.trim())
                            itemName = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF007AFF))
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        itemName = ""
                        showAddDialog = false
                    }
                ) { Text("Cancel") }
            }
        )
    }

    showEditDialog.let { (open, order) ->
        if (open && order != null) {
            var editItemName by remember { mutableStateOf(itemName) }
            var confirmEdit by remember { mutableStateOf(false) }

            if (!confirmEdit) {
                AlertDialog(
                    onDismissRequest = {
                        showEditDialog = false to null
                        itemName = ""
                    },
                    title = { Text("Edit Order", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    text = {
                        OutlinedTextField(
                            value = editItemName,
                            onValueChange = { editItemName = it },
                            label = { Text("Item Name") },
                            singleLine = true,
                            isError = editItemName.isBlank()
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (editItemName.isNotBlank()) {
                                    itemName = editItemName.trim()
                                    confirmEdit = true
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF007AFF))
                        ) { Text("Edit") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showEditDialog = false to null
                                itemName = ""
                            }
                        ) { Text("Cancel") }
                    }
                )
            } else {
                AlertDialog(
                    onDismissRequest = {
                        confirmEdit = false
                        showEditDialog = false to null
                        itemName = ""
                    },
                    title = {
                        Text(
                            "Confirm Edit",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = { Text("Are you sure you want to update this order?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.editOrder(order.copy(itemName = itemName))
                                confirmEdit = false
                                showEditDialog = false to null
                                itemName = ""
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF007AFF))
                        ) { Text("Confirm") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                confirmEdit = false
                                showEditDialog = false to null
                                itemName = ""
                            }
                        ) { Text("Cancel") }
                    }
                )
            }
        }
    }

    showDeleteDialog?.let { order ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Order", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${order.itemName}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOrder(order)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF007AFF))
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Orders") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    itemName = ""
                    showAddDialog = true
                },
                containerColor = Color(0xFF007AFF)
            ) {
                Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            orders.forEach { order ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Item:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = order.itemName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Status:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Status Icon",
                                tint = when (order.status.lowercase()) {
                                    "pending" -> Color(0xFFFFA500) // orange
                                    "completed" -> Color(0xFF4CAF50) // green
                                    "cancelled" -> Color.Red
                                    else -> Color.Gray
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = order.status,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = when (order.status.lowercase()) {
                                    "pending" -> Color(0xFFFFA500)
                                    "completed" -> Color(0xFF4CAF50)
                                    "cancelled" -> Color.Red
                                    else -> Color.Gray
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    itemName = order.itemName
                                    showEditDialog = true to order
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(
                                        0xFF007AFF
                                    )
                                )
                            ) {
                                Text("Edit")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = { showDeleteDialog = order },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

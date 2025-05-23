package com.example.ordermanagement.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ordermanagement.viewmodel.AdminViewModel
import com.example.ordermanagement.model.AuditLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditLogScreen(viewModel: AdminViewModel) {
    val logs by viewModel.auditLogs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAuditLogs()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Audit Log") }) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            logs.forEach { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Action: ${log.action}")
                        Text(text = "By: ${log.userEmail}")
                        Text(text = "Time: ${log.timestamp}")
                    }
                }
            }
        }
    }
}

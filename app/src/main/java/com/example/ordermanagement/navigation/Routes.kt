package com.example.ordermanagement.navigation

sealed class Routes(val route: String) {
    object Welcome : Routes("welcome")
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home") // может быть UserHome или AdminDashboard
    object UserHome : Routes("user_home")
    object AdminDashboard : Routes("admin_dashboard")
    object AuditLog : Routes("audit_log")

}

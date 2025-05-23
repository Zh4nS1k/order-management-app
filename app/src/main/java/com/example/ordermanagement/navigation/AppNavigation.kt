package com.example.ordermanagement.navigation
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.ordermanagement.ui.admin.AdminDashboard
import com.example.ordermanagement.ui.auth.*
import com.example.ordermanagement.ui.user.UserHomeScreen
import com.example.ordermanagement.viewmodel.AdminViewModel
import com.example.ordermanagement.viewmodel.AuthViewModel
import com.example.ordermanagement.viewmodel.UserViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Welcome.route) {
        composable(Routes.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(Routes.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Routes.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Routes.UserHome.route) {
            val userViewModel: UserViewModel = viewModel()
            UserHomeScreen(userViewModel)
        }
        composable("adminDashboard") {
            AdminDashboard(navController)
        }
        composable("admin_dashboard") { AdminDashboard(navController)
    }
}
}
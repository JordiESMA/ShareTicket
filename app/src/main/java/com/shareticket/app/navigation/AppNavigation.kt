package com.shareticket.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.shareticket.app.ui.screens.LoginScreen
import com.shareticket.app.ui.screens.TicketDetailScreen
import com.shareticket.app.ui.screens.TicketsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Tickets : Screen("ticketScreen")
    object TicketDetail : Screen("ticketDetail/{ticketId}") {
        fun createRoute(ticketId: String) = "ticketDetail/$ticketId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) Screen.Tickets.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Tickets.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Tickets.route) {
            TicketsScreen(
                onTicketClick = { ticketId ->
                    navController.navigate(Screen.TicketDetail.createRoute(ticketId))
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Tickets.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.TicketDetail.route,
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
            TicketDetailScreen(
                ticketId = ticketId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

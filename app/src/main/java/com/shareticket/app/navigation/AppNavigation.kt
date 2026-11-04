package com.shareticket.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shareticket.app.data.repository.AuthRepository
import com.shareticket.app.ui.screens.LoginScreen
import com.shareticket.app.ui.screens.TicketDetailScreen
import com.shareticket.app.ui.screens.TicketsScreen
import kotlinx.coroutines.launch

// Componente principal que gestiona la navegación entre las pantallas de la aplicación
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Determina la pantalla inicial: si el usuario ya inició sesión entra a "tickets", sino a "login"
    val inicio = if (AuthRepository.haySesion()) "tickets" else "login"

    NavHost(navController = navController, startDestination = inicio) {
        // Pantalla 1: Login
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("tickets") { popUpTo("login") { inclusive = true } }
            })
        }
        // Pantalla 2: Lista de Tickets
        composable("tickets") {
            TicketsScreen(
                onTicketClick = { id -> navController.navigate("detalle/$id") },
                onLogout = {
                    scope.launch {
                        AuthRepository.cerrarSesion(context)
                        navController.navigate("login") { popUpTo("tickets") { inclusive = true } }
                    }
                }
            )
        }
        // Pantalla 3: Detalle de un Ticket específico
        composable("detalle/{ticketId}") { entry ->
            val id = entry.arguments?.getString("ticketId") ?: ""
            TicketDetailScreen(ticketId = id, onBack = { navController.popBackStack() })
        }
    }
}

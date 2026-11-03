package com.shareticket.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.shareticket.app.data.repository.AuthRepository
import com.shareticket.app.data.repository.FirestoreRepository
import com.shareticket.app.ui.components.TicketItemRow
import kotlinx.coroutines.launch

@Composable
fun TicketDetailScreen(
    ticketId: String,
    onBack: () -> Unit
) {

    val scope = rememberCoroutineScope()

    var tickets by remember { mutableStateOf<List<com.shareticket.app.data.repository.Ticket>>(emptyList()) }

    fun getTickets() {
        scope.launch {
            tickets = FirestoreRepository.obtenerTickets(AuthRepository.usuarioActual()?.email ?: "")
        }
    }

    var ticketItems: List<com.shareticket.app.data.model.TicketItem> = emptyList()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = tickets[ticketId.toInt()].title,
            fontWeight = FontWeight.Bold
        )
    }
}

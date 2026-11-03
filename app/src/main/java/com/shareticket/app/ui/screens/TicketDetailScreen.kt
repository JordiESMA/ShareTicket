package com.shareticket.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val ticket = tickets.find { it.id == ticketId }

    var ticketItems = ticket?.items

    getTickets()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket?.title ?: "2",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                Button(
                    onClick = { onBack() },
                    modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    Text("Volver")
                }
            }

            Text(
                text = "Created by: ${AuthRepository.usuarioActual()?.email ?: ""}",
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketsDetailScreenPreview() {
    TicketDetailScreen(
        ticketId = "",
        onBack = {},
    )
}
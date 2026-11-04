package com.shareticket.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.shareticket.app.data.model.TicketItem
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

    var ticket by remember { mutableStateOf<com.shareticket.app.data.repository.Ticket?>(null) }

    var ticketItems by remember { mutableStateOf<List<TicketItem>>(emptyList()) }

    fun getTickets() {
        scope.launch {
            ticket = FirestoreRepository
                .obtenerTickets(AuthRepository.usuarioActual()?.email ?: "")
                .find { it.id == ticketId }
            ticketItems = FirestoreRepository.obtenerItems(ticketId)
        }
    }


    var inputText by remember { mutableStateOf("") }

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

            /** Text(
                text = "Created by: ${AuthRepository.usuarioActual()?.email ?: ""}",
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
            ) **/

        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(ticketItems.isEmpty()){
                Text(
                    text = "No hi ha items",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }else {
                ticketItems.let {
                    LazyColumn {
                        items(ticketItems, key = { it.id }) { item ->
                            TicketItemRow(
                                item = item,
                                onCheckedChange = { checked ->
                                    ticketItems = ticketItems.map {
                                        if (it.id == item.id) it.copy(isCompleted = checked) else it
                                    }
                                    scope.launch {
                                        FirestoreRepository.actualizarItem(ticketId, item.id, checked)
                                    }
                                },
                                onDelete = {
                                    ticketItems = ticketItems.filter { it.id != item.id }
                                    scope.launch {
                                        FirestoreRepository.eliminarItem(ticketId, item.id)
                                    }
                                }

                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(value = inputText, onValueChange = {
                    inputText = it
                })
                Button(onClick = {
                    if (inputText.isBlank()) return@Button
                    scope.launch {
                        if (FirestoreRepository.crearItem(ticketId, inputText.trim())) {
                            inputText = ""
                            getTickets()
                        }
                    }
                }) {
                    Text(text = "+")
                }
            }
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
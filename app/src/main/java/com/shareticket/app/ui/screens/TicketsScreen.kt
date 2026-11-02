package com.shareticket.app.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.shareticket.app.data.repository.AuthRepository
import com.shareticket.app.data.repository.FirestoreRepository
import com.shareticket.app.data.repository.Ticket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TicketsScreen(
    onTicketClick: (String) -> Unit,
    onLogout: () -> Unit
) {

    val tickets = remember {
        mutableStateListOf<Ticket>()
    }

    var inputText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                onClick = { onLogout() }
            ) {
                Text("Cerrar sesión")
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(tickets.isEmpty()){
                Text(
                    text = "No hi ha tickets",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }else {
                tickets.let {
                    LazyColumn(
                        content = {
                            items(it.size) { index ->
                                TicketRow(ticket = it[index])
                            }
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(value = inputText, onValueChange = {
                    inputText = it
                })
                Button(onClick = {
                    scope.launch {
                        FirestoreRepository.crearTicket(inputText)
                        tickets.add(Ticket(title = inputText))
                    }
                }) {
                    Text(text = "Add")
                }
            }
        }
    }
}

@Composable
fun TicketRow(ticket: Ticket) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        onClick = { }
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = ticket.title,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Created by: ${ticket.createdBy}",
                fontSize = 16.sp,
                color = Color.LightGray
            )
        }
    }
}

@Preview
@Composable
fun TicketsScerenPreview() {
    TicketsScreen(
        onTicketClick = {},
        onLogout = {}
    )
}
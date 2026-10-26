package com.shareticket.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shareticket.app.data.model.Ticket
import com.shareticket.app.data.model.TicketItem
import com.shareticket.app.data.model.getFakeTickets
import com.shareticket.app.ui.components.TicketItemRow
import com.shareticket.app.ui.theme.ShareTicketTheme

@Composable
fun TicketsScreen(
    onTicketClick: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: TicketViewModel = viewModel()
) {

    // Para probar que funciona bien, esto luego se reemplazara por los datos en el Firebase
    val tickets by viewModel.tickets.observeAsState()

    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        // if (tickets != null) {
            Text(
                text = "Els meus Tickets",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            tickets?.let {
                LazyColumn(
                    content = {
                        itemsIndexed(it) { index: Int, item: Ticket ->
                            /** Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = item.title,
                                        fontSize = 20.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Created by: ${item.createdBy}",
                                        fontSize = 16.sp,
                                        color = Color.LightGray
                                    )
                                }
                                IconButton(onClick = {
                                    itemIdNou = item.id
                                    editantTicket = true
                                }) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_menu_edit),
                                        contentDescription = "Modify Ticket",
                                        Modifier.size(20.dp),
                                        tint = Color.White
                                    )
                                }
                                IconButton(onClick = { item.onDelete(item.id) }) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_delete),
                                        contentDescription = "Delete Ticket",
                                        tint = Color.White
                                    )
                                }
                            } **/
                            TicketRow(
                                item = item,
                                onDelete = { viewModel.deleteTicket(item.id) }
                            )
                        }
                    }
                )
            }?: Text(
                text = "No hi ha tickets",
                modifier = Modifier.padding(top = 8.dp)
            )

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
                    viewModel.addTicket(inputText)
                    inputText = ""
                }) {
                    Text(text = "Add")
                }
            }



    }
}

@Composable
fun TicketRow(item: Ticket, onDelete: () -> Unit){

    var itemIdNou by remember { mutableStateOf("") }

    var editantTicket: Boolean by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = item.title,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Created by: ${item.createdBy}",
                fontSize = 16.sp,
                color = Color.LightGray
            )
        }
        IconButton(onClick = {
            itemIdNou = item.id
            editantTicket = true
        }) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_edit),
                contentDescription = "Modify Ticket",
                Modifier.size(20.dp),
                tint = Color.White
            )
        }
        IconButton(onClick = { onDelete }) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_delete),
                contentDescription = "Delete Ticket",
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketsScreenPreview() {
    ShareTicketTheme {
        TicketsScreen(
            onTicketClick = {},
            onLogout = {},
        )
    }
}

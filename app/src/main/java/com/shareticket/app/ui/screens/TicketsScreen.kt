package com.shareticket.app.ui.screens

import android.graphics.drawable.Icon
import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import com.shareticket.app.data.model.Ticket
import com.shareticket.app.data.model.TicketItem
import com.shareticket.app.ui.theme.ShareTicketTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun TicketsScreen(
    onTicketClick: (String) -> Unit,
    onLogout: () -> Unit
) {

    // Para probar que funciona bien, esto luego se reemplazara por los datos en el Firebase
    val tickets = remember {
        mutableStateListOf<TicketItem>(
            TicketItem("1", "Ticket 1", true, "User 1"),
            TicketItem("2", "Ticket 2", true, "User 2"),
            TicketItem("3", "Ticket 3", false,"User 3")
        )
    }

    var inputText by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        Text(
            text = "Els meus Tickets",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        tickets?.let {
            LazyColumn(
                content = {
                    itemsIndexed(tickets){ index: Int, ticket: TicketItem ->
                        TicketItem(
                            ticket = ticket,
                            onCheckedChange = { checked ->
                                tickets[index] = ticket.copy(isCompleted = checked)
                            }
                        )
                    }
                }
            )
        }?: Text(
            text = "No hay tickets",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
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
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun TicketItem(ticket : TicketItem, onCheckedChange: (Boolean) -> Unit){
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
                text = ticket.name,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Created by: ${ticket.addedBy}",
                fontSize = 16.sp,
                color = Color.LightGray
            )
        }
        IconButton(onClick = { deleteTicket(ticket.id.toInt()) }) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_delete),
                contentDescription = "Delete Ticket"
            )
        }
        IconButton(onClick = { }) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_edit),
                contentDescription = "Modify Ticket"
            )
        }
        Checkbox(
            checked = ticket.isCompleted,
            onCheckedChange = { checked ->
                onCheckedChange(checked)
            }
        )
    }
}

fun deleteTicket(id: Int){
    //TODO: Delete ticket from Firebase
}

@Preview(showBackground = true)
@Composable
fun TicketsScreenPreview() {
    ShareTicketTheme {
        TicketsScreen(
            onTicketClick = {},
            onLogout = {}
        )
    }
}

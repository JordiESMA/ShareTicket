package com.shareticket.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shareticket.app.data.model.TicketItem
import com.shareticket.app.ui.components.TicketItemRow
import com.shareticket.app.ui.theme.ShareTicketTheme

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
                    itemsIndexed(tickets){ index: Int, item: TicketItem ->
                        TicketItemRow(
                            item = item,
                            onCheckedChange = { checked -> tickets[index] = item.copy(isCompleted = checked) },
                            onDelete = { onDelete -> tickets[index] = tickets.removeAt(onDelete) })
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

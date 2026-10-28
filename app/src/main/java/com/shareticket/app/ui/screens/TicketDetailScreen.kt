package com.shareticket.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Surface
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shareticket.app.data.model.Ticket
import com.shareticket.app.data.model.TicketItem
import com.shareticket.app.data.model.getFakeTickets
import com.shareticket.app.ui.components.TicketItemRow
import com.shareticket.app.ui.theme.ShareTicketTheme

@Composable
fun TicketDetailScreen(
    ticketId: String,
    onBack: () -> Unit,
    viewModel: TicketViewModel = viewModel()
) {

    val tickets by viewModel.tickets.observeAsState()

    val TestTicket = Ticket(
        id = 1.toString(),
        title = "Ticket 1",
        createdBy = "User 1",
        creatorEmail = "william.henry.harrison@example-pet-store.com",
        items = listOf(
            TicketItem(
                id = "1",
                name = "Item 1",
                isCompleted = false,
                addedBy = "User 1"
            ),
            TicketItem(
                id = "2",
                name = "Item 2",
                isCompleted = true,
                addedBy = "User 2"
            ),
            TicketItem(
                id = "3",
                name = "Item 3",
                isCompleted = false,
                addedBy = "User 3"
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_revert),
                    contentDescription = "Tornar",
                )
            }
            Text(
                text = tickets?.get(ticketId.toInt())?.title ?: "Ticket not found",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
        TestTicket.items.let {
            LazyColumn(
                content = {
                    itemsIndexed(it) { index: Int, item: TicketItem ->
                        TicketItemRow(
                            item = item,
                            onCheckedChange = { checked -> item.isCompleted = checked },
                            onDelete = { itemId -> item.id = itemId },
                            onEdit = { onBack() }
                        )
                    }
                }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun TicketDetailScreenPreview() {
    ShareTicketTheme {
        TicketDetailScreen(
            ticketId = "1",
            onBack = {}
        )
    }
}

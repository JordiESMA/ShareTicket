package com.shareticket.app.data.model

import androidx.compose.runtime.mutableStateListOf

data class Ticket(
    val id: String = "",
    val title: String = "",
    val createdBy: String = "",
    val creatorEmail: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val items: List<TicketItem> = emptyList()
)

fun getFakeTickets(): List<Ticket>{
    var tickets = mutableStateListOf<Ticket>(
        Ticket(id = "0", title = "Ticket 1", createdBy = "User 1", creatorEmail = "william.henry.harrison@example-pet-store.com"),
        Ticket(id = "1", title = "Ticket 2", createdBy = "User 2", creatorEmail = "james.monroe@examplepetstore.com"),
        Ticket(id = "2", title = "Ticket 3", createdBy = "User 3", creatorEmail = "james.wilson@example-pet-store.com"),
    )
    return tickets
}

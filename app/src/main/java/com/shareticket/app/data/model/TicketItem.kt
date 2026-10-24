package com.shareticket.app.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

data class TicketItem(
    var id: String = "",
    var name: String = "",
    var isCompleted: Boolean = false,
    var addedBy: String = ""
)

fun getTicketItems(): List<TicketItem> {
    return mutableStateListOf(
        TicketItem("1", "Ticket 1", true, "User 1"),
        TicketItem("2", "Ticket 2", true, "User 2"),
        TicketItem("3", "Ticket 3", false, "User 3")
    )
}

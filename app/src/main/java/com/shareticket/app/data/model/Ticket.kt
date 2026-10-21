package com.shareticket.app.data.model

data class Ticket(
    val id: String = "",
    val title: String = "",
    val createdBy: String = "",
    val creatorEmail: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val items: List<TicketItem> = emptyList()
)

package com.shareticket.app.data.model

data class TicketItem(
    val id: String = "",
    val name: String = "",
    var isCompleted: Boolean = false,
    val addedBy: String = ""
)

package com.shareticket.app.ui.screens

import androidx.compose.runtime.mutableStateListOf
import com.shareticket.app.data.model.Ticket

object TicketManager {

    private val tickets = mutableStateListOf<Ticket>()

    fun getAllTickets(): List<Ticket> {
        return tickets
    }

    fun addTicket(title: String) {
        val id = (tickets.size + 1).toString()
        tickets.add(Ticket(id = id, title = title, createdBy = "User 1", creatorEmail = "samuel.chase@altostrat.com"))
    }

    fun deleteTicket(id: String) {
        tickets.removeIf {
            it.id == id
        }
    }

    fun editTicket(item: Ticket) {

    }

}
package com.shareticket.app.ui.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shareticket.app.data.model.Ticket

class TicketViewModel: ViewModel() {

    private var _tickets = MutableLiveData<List<Ticket>>()
    val tickets : LiveData<List<Ticket>> = _tickets

    fun getAllTickets(){
        _tickets.value = TicketManager.getAllTickets()
    }

    fun addTicket(title: String) {
        TicketManager.addTicket(title)
        getAllTickets()
    }

    fun deleteTicket(id: String) {
        TicketManager.deleteTicket(id)
        getAllTickets()
    }

}
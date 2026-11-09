package com.shareticket.app.data.model

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FireBaseRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun createTicket(title: String): String {
        val currentUser = auth.currentUser
        val ticketId = db.collection("tickets").document().id
        val ticket = Ticket(
            id = ticketId,
            title = title,
            createdBy = currentUser?.uid ?: "",
            creatorEmail = currentUser?.email ?: "anonymous",
            createdAt = System.currentTimeMillis(),
            items = emptyList()
        )
        db.collection("tickets").document(ticketId).set(ticket).await()
        return ticketId
    }

    suspend fun addTicketItem(ticketId: String, itemName: String) {
        val currentUser = auth.currentUser
        val ticketRef = db.collection("tickets").document(ticketId)
        
        db.runTransaction { transaction ->
            val snapshot = transaction.get(ticketRef)
            val ticket = snapshot.toObject(Ticket::class.java)
            if (ticket != null) {
                val newItem = TicketItem(
                    id = db.collection("tickets").document().id,
                    name = itemName,
                    isCompleted = false,
                    addedBy = currentUser?.email ?: "anonymous"
                )
                val updatedItems = ticket.items + newItem
                transaction.update(ticketRef, "items", updatedItems)
            }
        }.await()
    }
}

package com.shareticket.app.data.model

// Modelo de datos que representa una lista de compra o ticket
data class Ticket(
    val id: String = "",                  // Identificador único del ticket en Firestore
    val title: String = "",               // Título o nombre del ticket (ej: "Supermercado")
    val createdBy: String = "",           // ID del usuario que creó el ticket
    val creatorEmail: String = "",        // Correo electrónico del usuario creador
    val createdAt: Long = System.currentTimeMillis(), // Fecha de creación en milisegundos
    val items: List<TicketItem> = emptyList()         // Lista de artículos incluidos en este ticket
)

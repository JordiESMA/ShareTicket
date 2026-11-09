package com.shareticket.app.data.model

// Modelo de datos que representa un artículo individual dentro de un ticket
data class TicketItem(
    val id: String = "",             // Identificador único del artículo
    val name: String = "",           // Nombre del artículo (ej: "Leche", "Pan")
    var isCompleted: Boolean = false, // Indica si el producto ya fue comprado (marcado)
    val addedBy: String = "",        // Correo o ID de quien añadió el artículo
    val category: String = "General"  // Categoría del artículo (ej: "Lácteos", "Fruta", "Limpieza", "General")
)

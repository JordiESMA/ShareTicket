package com.shareticket.app.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

data class TicketItem(
    var id: String = "",
    var name: String = "",
    var isCompleted: Boolean = false,
    var addedBy: String = ""
)
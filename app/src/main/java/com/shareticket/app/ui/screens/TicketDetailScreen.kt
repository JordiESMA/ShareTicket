package com.shareticket.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shareticket.app.data.model.TicketItem
import com.shareticket.app.data.repository.AuthRepository
import com.shareticket.app.data.repository.FirestoreRepository
import com.shareticket.app.data.repository.Ticket
import com.shareticket.app.ui.components.TicketItemRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var ticket by remember { mutableStateOf<Ticket?>(null) }
    var ticketItems by remember { mutableStateOf<List<TicketItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }

    // Dialog states for sharing and shared-with list
    var showShareDialog by remember { mutableStateOf(false) }
    var shareEmailInput by remember { mutableStateOf("") }
    var showSharedWithDialog by remember { mutableStateOf(false) }

    fun getTicketsAndItems() {
        isLoading = true
        scope.launch {
            ticket = FirestoreRepository
                .obtenerTickets(AuthRepository.usuarioActual()?.email ?: "")
                .find { it.id == ticketId }
            ticketItems = FirestoreRepository.obtenerItems(ticketId)
            isLoading = false
        }
    }

    LaunchedEffect(ticketId) {
        getTicketsAndItems()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = ticket?.title ?: "Detalle del Ticket",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        getTicketsAndItems()
                        Toast.makeText(context, "Detalle actualizado", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                    IconButton(onClick = { showSharedWithDialog = true }) {
                        Icon(Icons.Default.Group, contentDescription = "Ver compartidos")
                    }
                    IconButton(onClick = {
                        shareEmailInput = ""
                        showShareDialog = true
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir ticket")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Main Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (isLoading && ticketItems.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else if (ticketItems.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay artículos en este ticket",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ticketItems, key = { it.id }) { item ->
                                TicketItemRow(
                                    item = item,
                                    onCheckedChange = { checked ->
                                        ticketItems = ticketItems.map {
                                            if (it.id == item.id) it.copy(isCompleted = checked) else it
                                        }
                                        scope.launch {
                                            FirestoreRepository.actualizarItem(ticketId, item.id, checked)
                                        }
                                    },
                                    onDelete = { itemId ->
                                        ticketItems = ticketItems.filter { it.id != itemId }
                                        scope.launch {
                                            FirestoreRepository.eliminarItem(ticketId, itemId)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Input Row for adding items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Añadir artículo...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            val name = inputText.trim()
                            if (name.isBlank()) return@Button
                            scope.launch {
                                if (FirestoreRepository.crearItem(ticketId, name)) {
                                    inputText = ""
                                    getTicketsAndItems()
                                } else {
                                    Toast.makeText(context, "Error al añadir artículo", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                    }
                }
            }
        }
    }

    // DIALOG: Compartir Ticket
    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("Compartir Ticket", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Introduce el correo electrónico de la persona con la que deseas compartir este ticket:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = shareEmailInput,
                        onValueChange = { shareEmailInput = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val email = shareEmailInput.trim()
                        if (email.isNotBlank()) {
                            scope.launch {
                                val success = FirestoreRepository.compartirTicket(ticketId, email)
                                if (success) {
                                    Toast.makeText(context, "Ticket compartido con éxito con $email", Toast.LENGTH_LONG).show()
                                    getTicketsAndItems()
                                } else {
                                    Toast.makeText(context, "No se encontró ningún usuario registrado con ese correo", Toast.LENGTH_LONG).show()
                                }
                                showShareDialog = false
                            }
                        } else {
                            Toast.makeText(context, "Introduce un correo válido", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Compartir", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG: Ver con quién está compartido este ticket
    if (showSharedWithDialog) {
        val sharedList = ticket?.sharedWith ?: emptyList()
        AlertDialog(
            onDismissRequest = { showSharedWithDialog = false },
            title = { Text("Compartido con", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Usuarios que tienen acceso a este ticket:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (sharedList.isEmpty()) {
                        Text(
                            text = "Este ticket no está compartido con nadie más.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(sharedList) { email ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = email,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSharedWithDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

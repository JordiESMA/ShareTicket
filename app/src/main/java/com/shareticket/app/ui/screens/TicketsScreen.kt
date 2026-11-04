package com.shareticket.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.material3.FloatingActionButton
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
import com.shareticket.app.data.repository.AuthRepository
import com.shareticket.app.data.repository.FirestoreRepository
import com.shareticket.app.data.repository.Ticket
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    onTicketClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var tickets by remember { mutableStateOf<List<Ticket>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Dialog state for sharing a ticket
    var sharingTicket by remember { mutableStateOf<Ticket?>(null) }
    var shareEmailInput by remember { mutableStateOf("") }

    // Dialog state for viewing who the ticket is shared with
    var viewingSharedTicket by remember { mutableStateOf<Ticket?>(null) }

    // Dialog state for creating a ticket
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTicketTitleInput by remember { mutableStateOf("") }

    fun getTickets() {
        isLoading = true
        scope.launch {
            tickets = FirestoreRepository.obtenerTickets(AuthRepository.usuarioActual()?.email ?: "")
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        getTickets()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tickets", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        getTickets()
                        Toast.makeText(context, "Tickets actualizados", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            AuthRepository.cerrarSesion(context)
                            onLogout()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Ticket")
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading && tickets.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (tickets.isEmpty()) {
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
                            text = "No tienes tickets de compra",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tickets) { ticket ->
                            TicketCard(
                                ticket = ticket,
                                onClick = { onTicketClick(ticket.id) },
                                onShareClick = {
                                    sharingTicket = ticket
                                    shareEmailInput = ""
                                },
                                onSharedWithClick = {
                                    viewingSharedTicket = ticket
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Compartir Ticket (pedir correo)
    sharingTicket?.let { ticket ->
        AlertDialog(
            onDismissRequest = { sharingTicket = null },
            title = { Text("Compartir Ticket", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Introduce el correo electrónico del usuario con quien deseas compartir '${ticket.title}':",
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
                                val success = FirestoreRepository.compartirTicket(ticket.id, email)
                                if (success) {
                                    Toast.makeText(context, "Ticket compartido con éxito con $email", Toast.LENGTH_LONG).show()
                                    getTickets()
                                } else {
                                    Toast.makeText(context, "No se encontró ningún usuario registrado con ese correo", Toast.LENGTH_LONG).show()
                                }
                                sharingTicket = null
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
                TextButton(onClick = { sharingTicket = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG: Ver con quién se ha compartido el ticket
    viewingSharedTicket?.let { ticket ->
        AlertDialog(
            onDismissRequest = { viewingSharedTicket = null },
            title = { Text("Compartido con", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Usuarios que tienen acceso a '${ticket.title}':",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (ticket.sharedWith.isEmpty()) {
                        Text(
                            text = "Este ticket no está compartido con nadie más.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(ticket.sharedWith) { userEmail ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = userEmail,
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
                TextButton(onClick = { viewingSharedTicket = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // DIALOG: Crear Nuevo Ticket
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Crear Ticket", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newTicketTitleInput,
                    onValueChange = { newTicketTitleInput = it },
                    label = { Text("Nombre del ticket") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val title = newTicketTitleInput.trim()
                        if (title.isNotBlank()) {
                            scope.launch {
                                if (FirestoreRepository.crearTicket(title)) {
                                    Toast.makeText(context, "Ticket creado con éxito", Toast.LENGTH_SHORT).show()
                                    newTicketTitleInput = ""
                                    showCreateDialog = false
                                    getTickets()
                                } else {
                                    Toast.makeText(context, "Error al crear el ticket", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                ) {
                    Text("Crear", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit,
    onShareClick: () -> Unit,
    onSharedWithClick: () -> Unit
) {
    var creatorEmail by remember(ticket.createdBy) { mutableStateOf("") }

    LaunchedEffect(ticket.createdBy) {
        if (ticket.createdBy.isNotBlank()) {
            creatorEmail = FirestoreRepository.obtenerEmailUsuario(ticket.createdBy)
                ?: ticket.createdBy
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ticket.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Creado por: ${creatorEmail.ifEmpty { "Desconocido" }}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onSharedWithClick) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Ver compartidos",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onShareClick) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir ticket",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.shareticket.app.data.repository.Friend
import com.shareticket.app.data.repository.FriendRequest
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
    var shareInput by remember { mutableStateOf("") }

    // Dialog state for viewing who the ticket is shared with
    var viewingSharedTicket by remember { mutableStateOf<Ticket?>(null) }

    // Dialog state for creating a ticket
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTicketTitleInput by remember { mutableStateOf("") }

    // Friends State
    var showFriendsDialog by remember { mutableStateOf(false) }
    var friendsList by remember { mutableStateOf<List<Friend>>(emptyList()) }
    var pendingRequests by remember { mutableStateOf<List<FriendRequest>>(emptyList()) }
    var newFriendEmailInput by remember { mutableStateOf("") }
    var friendsTabIndex by remember { mutableStateOf(0) }

    // Dialog state for editing friend alias
    var editingFriendAlias by remember { mutableStateOf<Friend?>(null) }
    var friendAliasInput by remember { mutableStateOf("") }

    fun getTickets() {
        isLoading = true
        scope.launch {
            tickets = FirestoreRepository.obtenerTickets(AuthRepository.usuarioActual()?.email ?: "")
            isLoading = false
        }
    }

    fun loadFriendsData() {
        scope.launch {
            friendsList = FirestoreRepository.obtenerAmigos()
            pendingRequests = FirestoreRepository.obtenerSolicitudesPendientes()
        }
    }

    LaunchedEffect(Unit) {
        getTickets()
        loadFriendsData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tickets", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        showFriendsDialog = true
                        loadFriendsData()
                    }) {
                        Icon(Icons.Default.People, contentDescription = "Menú Amigos", tint = MaterialTheme.colorScheme.primary)
                    }
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
                                    shareInput = ""
                                    loadFriendsData()
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

    // DIALOG: Menú de Amigos
    if (showFriendsDialog) {
        AlertDialog(
            onDismissRequest = { showFriendsDialog = false },
            title = { Text("Menú de Amigos", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    TabRow(selectedTabIndex = friendsTabIndex) {
                        Tab(
                            selected = friendsTabIndex == 0,
                            onClick = { friendsTabIndex = 0 },
                            text = { Text("Amigos (${friendsList.size})") }
                        )
                        Tab(
                            selected = friendsTabIndex == 1,
                            onClick = { friendsTabIndex = 1 },
                            text = { Text("Solicitudes (${pendingRequests.size})") }
                        )
                        Tab(
                            selected = friendsTabIndex == 2,
                            onClick = { friendsTabIndex = 2 },
                            text = { Text("Añadir") }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    when (friendsTabIndex) {
                        0 -> {
                            if (friendsList.isEmpty()) {
                                Text("Aún no tienes amigos agregados.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                LazyColumn(
                                    modifier = Modifier.height(200.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(friendsList) { friend ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = if (friend.alias.isNotBlank()) friend.alias else friend.email,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (friend.alias.isNotBlank()) {
                                                        Text(text = friend.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                                IconButton(onClick = {
                                                    editingFriendAlias = friend
                                                    friendAliasInput = friend.alias
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Editar Alias", tint = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            if (pendingRequests.isEmpty()) {
                                Text("No tienes solicitudes pendientes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                LazyColumn(
                                    modifier = Modifier.height(200.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(pendingRequests) { request ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = request.fromEmail,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Row {
                                                    IconButton(onClick = {
                                                        scope.launch {
                                                            if (FirestoreRepository.responderSolicitudAmistad(request, true)) {
                                                                Toast.makeText(context, "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                                                                loadFriendsData()
                                                            }
                                                        }
                                                    }) {
                                                        Icon(Icons.Default.Check, contentDescription = "Aceptar", tint = MaterialTheme.colorScheme.primary)
                                                    }
                                                    IconButton(onClick = {
                                                        scope.launch {
                                                            if (FirestoreRepository.responderSolicitudAmistad(request, false)) {
                                                                Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                                                                loadFriendsData()
                                                            }
                                                        }
                                                    }) {
                                                        Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = MaterialTheme.colorScheme.error)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            Column {
                                OutlinedTextField(
                                    value = newFriendEmailInput,
                                    onValueChange = { newFriendEmailInput = it },
                                    label = { Text("Correo del amigo") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val email = newFriendEmailInput.trim()
                                        if (email.isNotBlank()) {
                                            scope.launch {
                                                val msg = FirestoreRepository.enviarSolicitudAmistad(email)
                                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                newFriendEmailInput = ""
                                                loadFriendsData()
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Enviar Solicitud")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFriendsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // DIALOG: Editar Alias de Amigo
    editingFriendAlias?.let { friend ->
        AlertDialog(
            onDismissRequest = { editingFriendAlias = null },
            title = { Text("Asignar Alias", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Asigna un apodo o alias para ${friend.email}:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = friendAliasInput,
                        onValueChange = { friendAliasInput = it },
                        label = { Text("Alias (ej. Juan, Mamá)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            if (FirestoreRepository.guardarAliasAmigo(friend.email, friendAliasInput)) {
                                Toast.makeText(context, "Alias guardado", Toast.LENGTH_SHORT).show()
                                loadFriendsData()
                            }
                            editingFriendAlias = null
                        }
                    }
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingFriendAlias = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG: Compartir Ticket (por Alias o Email)
    sharingTicket?.let { ticket ->
        AlertDialog(
            onDismissRequest = { sharingTicket = null },
            title = { Text("Compartir Ticket", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Escribe el correo o alias del amigo para compartir '${ticket.title}':",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (friendsList.isNotEmpty()) {
                        Text("Tus amigos (toca para seleccionar):", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(friendsList) { friend ->
                                val label = if (friend.alias.isNotBlank()) friend.alias else friend.email
                                FilterChip(
                                    selected = shareInput == label || shareInput == friend.email,
                                    onClick = { shareInput = label },
                                    label = { Text(label) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = shareInput,
                        onValueChange = { shareInput = it },
                        label = { Text("Correo o Alias del amigo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val input = shareInput.trim()
                        if (input.isNotBlank()) {
                            scope.launch {
                                val success = FirestoreRepository.compartirTicket(ticket.id, input)
                                if (success) {
                                    Toast.makeText(context, "Ticket compartido con éxito", Toast.LENGTH_LONG).show()
                                    getTickets()
                                } else {
                                    Toast.makeText(context, "No se encontró ningún usuario con ese correo o alias", Toast.LENGTH_LONG).show()
                                }
                                sharingTicket = null
                            }
                        } else {
                            Toast.makeText(context, "Introduce un correo o alias", Toast.LENGTH_SHORT).show()
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
                                val matchFriend = friendsList.find { it.email.equals(userEmail, ignoreCase = true) }
                                val displayText = if (matchFriend != null && matchFriend.alias.isNotBlank()) {
                                    "${matchFriend.alias} ($userEmail)"
                                } else {
                                    userEmail
                                }
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = displayText,
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

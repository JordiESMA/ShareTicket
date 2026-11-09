package com.shareticket.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import com.shareticket.app.data.repository.Friend
import com.shareticket.app.data.repository.Ticket
import com.shareticket.app.ui.components.TicketItemRow
import kotlinx.coroutines.launch

// Categorías generales predefinidas
private val CATEGORIAS_GENERALES = listOf(
    "Alimentación",
    "Carnes y Pescados",
    "Lácteos y Huevos",
    "Bebidas",
    "Hogar y Limpieza",
    "Electrodomésticos y Tecnología",
    "Escolar y Oficina",
    "Otros"
)

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
    var friendsList by remember { mutableStateOf<List<Friend>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Selector de vista en pantalla (0 = Todos los elementos, 1 = Por categorías)
    var selectedViewTabIndex by remember { mutableStateOf(0) }

    // Estado del Pop-up (AlertDialog) para añadir nuevo artículo
    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemNameInput by remember { mutableStateOf("") }
    var newItemCategoryInput by remember { mutableStateOf("Alimentación") }

    // Dialog states for sharing and shared-with list
    var showShareDialog by remember { mutableStateOf(false) }
    var shareInput by remember { mutableStateOf("") }
    var showSharedWithDialog by remember { mutableStateOf(false) }

    fun getTicketsAndItems() {
        isLoading = true
        scope.launch {
            ticket = FirestoreRepository
                .obtenerTickets(AuthRepository.usuarioActual()?.email ?: "")
                .find { it.id == ticketId }
            ticketItems = FirestoreRepository.obtenerItems(ticketId)
            friendsList = FirestoreRepository.obtenerAmigos()
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
                        shareInput = ""
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
        },
        floatingActionButton = {
            // Botón flotante para abrir el Pop-up de añadir artículo
            FloatingActionButton(
                onClick = {
                    newItemNameInput = ""
                    newItemCategoryInput = "Alimentación"
                    showAddItemDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Artículo")
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // SELECTOR DE VISTA CON LETRA EN PANTALLA
                PrimaryTabRow(
                    selectedTabIndex = selectedViewTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedViewTabIndex == 0,
                        onClick = { selectedViewTabIndex = 0 },
                        text = {
                            Text(
                                text = "Elementos (${ticketItems.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedViewTabIndex == 1,
                        onClick = { selectedViewTabIndex = 1 },
                        text = {
                            Text(
                                text = "Por Categorías",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    )
                }

                // CONTENIDO SEGÚN LA VISTA SELECCIONADA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
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
                    } else if (selectedViewTabIndex == 1) {
                        // VISTA: POR CATEGORÍAS
                        val groupedItems = ticketItems.groupBy {
                            if (it.category.isBlank()) "Otros" else it.category
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            groupedItems.forEach { (categoryName, itemsInCategory) ->
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text(
                                            text = "📁 $categoryName (${itemsInCategory.size})",
                                            modifier = Modifier.padding(12.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                items(itemsInCategory, key = { it.id }) { item ->
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
                    } else {
                        // VISTA: TODOS LOS ELEMENTOS
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
            }
        }
    }

    // POP-UP DIALOG: Añadir Artículo con Nombre y Categoría
    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Añadir Artículo", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newItemNameInput,
                        onValueChange = { newItemNameInput = it },
                        label = { Text("Nombre del artículo (ej. Leche)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selecciona una categoría:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CATEGORIAS_GENERALES) { cat ->
                            FilterChip(
                                selected = newItemCategoryInput == cat,
                                onClick = { newItemCategoryInput = cat },
                                label = { Text(cat, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = newItemNameInput.trim()
                        if (name.isNotBlank()) {
                            scope.launch {
                                val success = FirestoreRepository.crearItem(ticketId, name, newItemCategoryInput)
                                if (success) {
                                    Toast.makeText(context, "Artículo añadido", Toast.LENGTH_SHORT).show()
                                    showAddItemDialog = false
                                    getTicketsAndItems()
                                } else {
                                    Toast.makeText(context, "Error al añadir artículo", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Escribe el nombre del artículo", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Añadir", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG: Compartir Ticket
    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("Compartir Ticket", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Introduce el correo o alias del amigo con quien deseas compartir este ticket:",
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
                                val success = FirestoreRepository.compartirTicket(ticketId, input)
                                if (success) {
                                    Toast.makeText(context, "Ticket compartido con éxito", Toast.LENGTH_LONG).show()
                                    getTicketsAndItems()
                                } else {
                                    Toast.makeText(context, "No se encontró ningún usuario con ese correo o alias", Toast.LENGTH_LONG).show()
                                }
                                showShareDialog = false
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
                                val matchFriend = friendsList.find { it.email.equals(email, ignoreCase = true) }
                                val displayText = if (matchFriend != null && matchFriend.alias.isNotBlank()) {
                                    "${matchFriend.alias} ($email)"
                                } else {
                                    email
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
                TextButton(onClick = { showSharedWithDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

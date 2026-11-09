package com.shareticket.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shareticket.app.data.model.TicketItem

// Componente reutilizable para mostrar cada fila de un artículo dentro de la lista de un ticket
@Composable
fun TicketItemRow(
    item: TicketItem,
    onCheckedChange: (Boolean) -> Unit, // Acción al marcar o desmarcar la casilla
    onDelete: (String) -> Unit          // Acción al pulsar el botón de borrar
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Casilla de verificación (Checkbox)
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { checked -> onCheckedChange(checked) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Nombre, categoría y detalles del artículo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                }

                if (item.category.isNotBlank() && item.category != "General") {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📁 ${item.category}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (item.addedBy.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Añadido por: ${item.addedBy}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botón con icono de papelera para eliminar el artículo
            IconButton(onClick = { onDelete(item.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar artículo",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

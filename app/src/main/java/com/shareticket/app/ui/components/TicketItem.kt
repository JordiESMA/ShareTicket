package com.shareticket.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shareticket.app.data.model.TicketItem

@Composable
fun TicketItemRow(
    item: TicketItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = { checked ->
                onCheckedChange(checked)
            }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)

        ) {
            Text(
                text = item.name,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Created by: ${item.addedBy}",
                fontSize = 16.sp,
                color = Color.LightGray
            )
        }
        IconButton(onClick = { onDelete(item.id) }) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_delete),
                contentDescription = "Delete Ticket",
                tint = Color.White
            )
        }
        IconButton(onClick = { onEdit("1") }) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_edit),
                contentDescription = "Modify Ticket",
                Modifier.size(20.dp),
                tint = Color.White
            )
        }
    }



    /**
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
    **/
}

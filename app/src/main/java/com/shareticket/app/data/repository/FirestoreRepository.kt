package com.shareticket.app.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.shareticket.app.data.model.TicketItem
import kotlinx.coroutines.tasks.await

data class Ticket(
    val id: String = "",
    val title: String = "",
    val createdBy: String = "",
    val sharedWith: List<String> = emptyList(),
    val items: List<TicketItem> = emptyList()
)

object FirestoreRepository {

    private val db = Firebase.firestore

    // USUARIOS
    suspend fun guardarUsuario(user: FirebaseUser) {
        db.collection("users").document(user.uid).set(
            mapOf("name" to user.displayName, "email" to user.email)
        ).await()
    }

    suspend fun obtenerEmailUsuario(uid: String): String? {
        return try {
            db.collection("users").document(uid).get().await().getString("email")
        } catch (e: Exception) {
            null
        }
    }

    // TICKETS
    suspend fun crearTicket(title: String): Boolean {
        val user = AuthRepository.usuarioActual() ?: return false
        return try {
            db.collection("tickets").add(
                mapOf(
                    "title" to title,
                    "createdBy" to user.uid,
                    "sharedWith" to listOf(user.email),
                    "items" to listOf<TicketItem>()
                )
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerTickets(email: String): List<Ticket> {
        return try {
            val result = db.collection("tickets")
                .whereArrayContains("sharedWith", email)
                .get().await()
            result.documents.map { doc ->
                Ticket(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    createdBy = doc.getString("createdBy") ?: "",
                    sharedWith = (doc.get("sharedWith") as? List<*>)
                        ?.filterIsInstance<String>() ?: emptyList()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // COMPARTIR
    suspend fun compartirTicket(ticketId: String, correo: String): Boolean {
        return try {
            val existe = db.collection("users")
                .whereEqualTo("email", correo)
                .get().await()
            if (existe.isEmpty) return false

            db.collection("tickets").document(ticketId)
                .update("sharedWith", FieldValue.arrayUnion(correo)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // TICKETITEMS
    suspend fun crearItem(ticketId: String, name: String): Boolean {
        return try {
            db.collection("tickets").document(ticketId)
                .collection("items")
                .add(
                    mapOf(
                        "name" to name,
                        "isCompleted" to false,
                        "addedBy" to AuthRepository.usuarioActual()?.uid
                    )
                ).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("ITEMS", "crearItem", e)
            false
        }
    }

    suspend fun obtenerItems(ticketId: String): List<TicketItem> {
        return try {
            db.collection("tickets").document(ticketId)
                .collection("items")
                .get().await()
                .documents.map { doc ->
                    TicketItem(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        isCompleted = doc.getBoolean("isCompleted") ?: false
                    )
                }
        } catch (e: Exception) {
            android.util.Log.e("ITEMS", "obtenerItems", e)
            emptyList()
        }
    }

    suspend fun actualizarItem(ticketId: String, itemId: String, completed: Boolean): Boolean {
        return try {
            db.collection("tickets").document(ticketId)
                .collection("items").document(itemId)
                .update("isCompleted", completed).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarItem(ticketId: String, itemId: String): Boolean {
        return try {
            db.collection("tickets").document(ticketId)
                .collection("items").document(itemId)
                .delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

}
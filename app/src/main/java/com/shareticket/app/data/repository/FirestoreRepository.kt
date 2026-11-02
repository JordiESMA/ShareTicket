package com.shareticket.app.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class Ticket(
    val id: String = "",
    val title: String = "",
    val createdBy: String = "",
    val sharedWith: List<String> = emptyList()
)

object FirestoreRepository {

    private val db = Firebase.firestore

    // USUARIOS
    suspend fun guardarUsuario(user: FirebaseUser) {
        db.collection("users").document(user.uid).set(
            mapOf("name" to user.displayName, "email" to user.email)
        ).await()
    }

    // TICKETS
    suspend fun crearTicket(title: String): Boolean {
        val user = AuthRepository.usuarioActual() ?: return false
        return try {
            db.collection("tickets").add(
                mapOf(
                    "title" to title,
                    "createdBy" to user.uid,
                    "sharedWith" to listOf(user.email)
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
}
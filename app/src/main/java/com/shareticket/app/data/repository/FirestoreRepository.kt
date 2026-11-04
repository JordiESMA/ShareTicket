package com.shareticket.app.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.shareticket.app.data.model.TicketItem
import kotlinx.coroutines.tasks.await

// Modelo de datos para un Ticket de compra
data class Ticket(
    val id: String = "",
    val title: String = "",
    val createdBy: String = "",
    val sharedWith: List<String> = emptyList(),
    val items: List<TicketItem> = emptyList()
)

// Modelo de datos para guardar la información de un Amigo
data class Friend(
    val email: String = "",
    val alias: String = ""
)

// Modelo de datos para guardar una Solicitud de Amistad
data class FriendRequest(
    val id: String = "",
    val fromEmail: String = "",
    val toEmail: String = "",
    val status: String = "pending"
)

// Repositorio encargado de gestionar todas las operaciones con Cloud Firestore (Base de Datos)
object FirestoreRepository {

    private val db = Firebase.firestore

    // ----------------------------------------------------
    // GESTIÓN DE USUARIOS
    // ----------------------------------------------------

    // Guarda los datos básicos del usuario autenticado en la colección "users"
    suspend fun guardarUsuario(user: FirebaseUser) {
        db.collection("users").document(user.uid).set(
            mapOf("name" to user.displayName, "email" to user.email)
        ).await()
    }

    // Obtiene el correo electrónico de un usuario mediante su UID
    suspend fun obtenerEmailUsuario(uid: String): String? {
        return try {
            db.collection("users").document(uid).get().await().getString("email")
        } catch (e: Exception) {
            null
        }
    }

    // ----------------------------------------------------
    // GESTIÓN DE AMISTADES (AMIGOS Y SOLICITUDES)
    // ----------------------------------------------------

    // Envía una solicitud de amistad al correo indicado
    suspend fun enviarSolicitudAmistad(toEmail: String): String {
        val currentUser = AuthRepository.usuarioActual() ?: return "Debes estar autenticado"
        val myEmail = currentUser.email ?: return "Email no válido"
        val targetEmail = toEmail.trim().lowercase()

        if (targetEmail.isEmpty()) return "Introduce un correo válido"
        if (targetEmail == myEmail.lowercase()) return "No puedes enviarte una solicitud a ti mismo"

        return try {
            // Verificar si el correo destino existe en la base de datos
            val userExists = db.collection("users")
                .whereEqualTo("email", targetEmail)
                .get().await()

            if (userExists.isEmpty) {
                return "No se encontró ningún usuario con ese correo"
            }

            // Guardar el registro de la solicitud en la colección "friend_requests"
            val reqRef = db.collection("friend_requests").document()
            reqRef.set(
                mapOf(
                    "id" to reqRef.id,
                    "fromEmail" to myEmail,
                    "toEmail" to targetEmail,
                    "status" to "pending"
                )
            ).await()

            "Solicitud de amistad enviada a $targetEmail"
        } catch (e: Exception) {
            "Error al enviar la solicitud: ${e.localizedMessage}"
        }
    }

    // Obtiene la lista de solicitudes de amistad pendientes dirigidas al usuario actual
    suspend fun obtenerSolicitudesPendientes(): List<FriendRequest> {
        val myEmail = AuthRepository.usuarioActual()?.email ?: return emptyList()
        return try {
            val result = db.collection("friend_requests")
                .whereEqualTo("toEmail", myEmail.lowercase())
                .whereEqualTo("status", "pending")
                .get().await()

            result.documents.map { doc ->
                FriendRequest(
                    id = doc.id,
                    fromEmail = doc.getString("fromEmail") ?: "",
                    toEmail = doc.getString("toEmail") ?: "",
                    status = doc.getString("status") ?: "pending"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Responde (Acepta o Rechaza) una solicitud de amistad
    suspend fun responderSolicitudAmistad(request: FriendRequest, aceptar: Boolean): Boolean {
        val currentUser = AuthRepository.usuarioActual() ?: return false
        val myEmail = currentUser.email ?: return false

        return try {
            if (aceptar) {
                // Actualizar estado de la solicitud a aceptada
                db.collection("friend_requests").document(request.id)
                    .update("status", "accepted").await()

                // Agregar el amigo a la subcolección de amigos del usuario actual
                val myFriendDocId = request.fromEmail.replace(".", "_")
                db.collection("users").document(currentUser.uid)
                    .collection("friends").document(myFriendDocId)
                    .set(mapOf("email" to request.fromEmail, "alias" to "")).await()

                // Agregar el usuario actual a la subcolección de amigos del emisor
                val senderUserQuery = db.collection("users")
                    .whereEqualTo("email", request.fromEmail)
                    .get().await()

                if (!senderUserQuery.isEmpty) {
                    val senderUid = senderUserQuery.documents.first().id
                    val senderFriendDocId = myEmail.replace(".", "_")
                    db.collection("users").document(senderUid)
                        .collection("friends").document(senderFriendDocId)
                        .set(mapOf("email" to myEmail, "alias" to "")).await()
                }
            } else {
                // Rechazar la solicitud
                db.collection("friend_requests").document(request.id)
                    .update("status", "rejected").await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Obtiene la lista de amigos del usuario actual
    suspend fun obtenerAmigos(): List<Friend> {
        val currentUser = AuthRepository.usuarioActual() ?: return emptyList()
        return try {
            val result = db.collection("users").document(currentUser.uid)
                .collection("friends")
                .get().await()

            result.documents.map { doc ->
                Friend(
                    email = doc.getString("email") ?: "",
                    alias = doc.getString("alias") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Guarda o actualiza un apodo (alias) personalizado para un amigo
    suspend fun guardarAliasAmigo(friendEmail: String, alias: String): Boolean {
        val currentUser = AuthRepository.usuarioActual() ?: return false
        val docId = friendEmail.replace(".", "_")
        return try {
            db.collection("users").document(currentUser.uid)
                .collection("friends").document(docId)
                .set(mapOf("email" to friendEmail, "alias" to alias.trim())).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Traduce un alias a su dirección de correo real si coincide con algún amigo
    suspend fun resolverAliasOEmail(aliasOEmail: String): String {
        val input = aliasOEmail.trim()
        if (input.isEmpty()) return input
        val amigos = obtenerAmigos()
        val amigoEncontrado = amigos.find {
            it.alias.equals(input, ignoreCase = true) || it.email.equals(input, ignoreCase = true)
        }
        return amigoEncontrado?.email ?: input
    }

    // ----------------------------------------------------
    // GESTIÓN DE TICKETS DE COMPRA
    // ----------------------------------------------------

    // Crea un nuevo ticket de compra en la colección "tickets"
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

    // Obtiene todos los tickets en los que el correo del usuario esté incluido en "sharedWith"
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

    // Comparte un ticket con un usuario dado su correo o su alias
    suspend fun compartirTicket(ticketId: String, aliasOEmail: String): Boolean {
        return try {
            val correo = resolverAliasOEmail(aliasOEmail)
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

    // ----------------------------------------------------
    // GESTIÓN DE ARTÍCULOS (ITEMS) DE UN TICKET
    // ----------------------------------------------------

    // Crea un nuevo artículo en la subcolección "items" dentro del ticket
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
            android.util.Log.e("ITEMS", "Error al crear item: ${e.message}", e)
            false
        }
    }

    // Obtiene los artículos del ticket especificado
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
            android.util.Log.e("ITEMS", "Error al obtener items: ${e.message}", e)
            emptyList()
        }
    }

    // Marca o desmarca un artículo como comprado (isCompleted)
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

    // Elimina un artículo del ticket
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

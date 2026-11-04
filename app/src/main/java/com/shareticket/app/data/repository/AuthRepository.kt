package com.shareticket.app.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.shareticket.app.R
import kotlinx.coroutines.tasks.await

// Repositorio encargado del inicio y cierre de sesión con Firebase Auth y Google Credential Manager
object AuthRepository {

    // Devuelve el usuario actualmente autenticado en Firebase
    fun usuarioActual(): FirebaseUser? = Firebase.auth.currentUser

    // Indica si hay una sesión activa de usuario
    fun haySesion(): Boolean = usuarioActual() != null

    // Inicia sesión con la cuenta de Google seleccionada por el usuario
    suspend fun loginConGoogle(context: Context): Boolean {
        return try {
            // Configurar la opción de autenticación con Google ID
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()

            // Crear la solicitud de credenciales
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()

            // Abrir el diálogo nativo de cuentas de Google del dispositivo
            val result = CredentialManager.create(context).getCredential(context, request)
            val google = GoogleIdTokenCredential.createFrom(result.credential.data)
            val credencial = GoogleAuthProvider.getCredential(google.idToken, null)

            // Autenticar en Firebase con el token de Google obtenido
            Firebase.auth.signInWithCredential(credencial).await()

            // Guardar o actualizar la información del usuario en Firestore
            FirestoreRepository.guardarUsuario(usuarioActual()!!)
            true
        } catch (e: Exception) {
            android.util.Log.e("LOGIN", "Error al iniciar sesión: ${e.message}", e)
            false
        }
    }

    // Cierra la sesión activa en Firebase y limpia Credential Manager
    suspend fun cerrarSesion(context: Context) {
        Firebase.auth.signOut()
        CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
    }
}

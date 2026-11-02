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

object AuthRepository {

    fun usuarioActual(): FirebaseUser? = Firebase.auth.currentUser

    fun haySesion(): Boolean = usuarioActual() != null

    suspend fun loginConGoogle(context: Context): Boolean {
        return try {
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()

            val result = CredentialManager.create(context).getCredential(context, request)
            val google = GoogleIdTokenCredential.createFrom(result.credential.data)
            val credencial = GoogleAuthProvider.getCredential(google.idToken, null)
            Firebase.auth.signInWithCredential(credencial).await()

            FirestoreRepository.guardarUsuario(usuarioActual()!!)
            true
        } catch (e: Exception) {
            android.util.Log.e("LOGIN", "Error: ${e::class.simpleName} - ${e.message}", e)
            false
        }
    }

    suspend fun cerrarSesion(context: Context) {
        Firebase.auth.signOut()
        CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
    }
}
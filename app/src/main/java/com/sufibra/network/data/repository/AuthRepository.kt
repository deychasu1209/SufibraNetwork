package com.sufibra.network.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val uid = result.user?.uid

            if (uid != null) {
                Result.success(uid)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    suspend fun changeCurrentUserPassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No hay una sesión activa"))

            val email = currentUser.email
                ?: return Result.failure(Exception("No se pudo identificar el correo del usuario"))

            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            currentUser.reauthenticate(credential).await()
            currentUser.updatePassword(newPassword).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}

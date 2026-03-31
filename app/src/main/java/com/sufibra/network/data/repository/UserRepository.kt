package com.sufibra.network.data.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sufibra.network.domain.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getCurrentUser(): Result<User> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("No hay una sesión activa"))

        return getUserByUid(uid)
    }

    suspend fun getUserByUid(uid: String): Result<User> {
        return try {
            val document = firestore
                .collection("usuarios")
                .document(uid)
                .get()
                .await()

            if (document.exists()) {
                val user = User(
                    idUsuario = document.getString("idUsuario") ?: "",
                    nombres = document.getString("nombres") ?: "",
                    apellidos = document.getString("apellidos") ?: "",
                    correo = document.getString("correo") ?: "",
                    rol = document.getString("rol") ?: "",
                    estado = document.getBoolean("estado") ?: false,
                    fechaCreacion = document.getLong("fechaCreacion") ?: 0L,
                    telefono = document.getString("telefono"),
                    zonaAsignada = document.getString("zonaAsignada"),
                    disponible = document.getBoolean("disponible")
                )

                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado en Firestore"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUser(
        context: Context,
        nombres: String,
        apellidos: String,
        correo: String,
        password: String,
        rol: String,
        telefono: String? = null,
        zonaAsignada: String? = null
    ): Result<User> {
        return try {
            val normalizedNames = normalizeSpaces(nombres)
            val normalizedLastNames = normalizeSpaces(apellidos)
            val normalizedEmail = correo.trim()
            val normalizedPhone = telefono?.trim()?.takeIf { it.isNotBlank() }
            val normalizedZone = zonaAsignada?.let(::normalizeSpaces)?.takeIf { it.isNotBlank() }

            val primaryApp = FirebaseApp.getInstance()
            val options = primaryApp.options

            val secondaryApp = FirebaseApp.initializeApp(
                context,
                options,
                "SecondaryApp"
            )!!

            val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

            val authResult = secondaryAuth
                .createUserWithEmailAndPassword(normalizedEmail, password)
                .await()

            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("No se pudo crear el usuario"))

            val user = User(
                idUsuario = uid,
                nombres = normalizedNames,
                apellidos = normalizedLastNames,
                correo = normalizedEmail,
                rol = rol,
                estado = true,
                fechaCreacion = System.currentTimeMillis(),
                telefono = normalizedPhone,
                zonaAsignada = normalizedZone,
                disponible = if (rol == "TECHNICIAN") true else null
            )

            firestore.collection("usuarios")
                .document(uid)
                .set(user)
                .await()

            secondaryAuth.signOut()
            secondaryApp.delete()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val snapshot = firestore
                .collection("usuarios")
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { document ->
                document.toObject(User::class.java)
            }

            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val normalizedUser = user.copy(
                nombres = normalizeSpaces(user.nombres),
                apellidos = normalizeSpaces(user.apellidos),
                telefono = user.telefono?.trim()?.takeIf { it.isNotBlank() },
                zonaAsignada = user.zonaAsignada?.let(::normalizeSpaces)?.takeIf { it.isNotBlank() }
            )

            firestore.collection("usuarios")
                .document(user.idUsuario)
                .set(normalizedUser)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserStatus(
        uid: String,
        estado: Boolean
    ): Result<Unit> {
        return try {
            firestore.collection("usuarios")
                .document(uid)
                .update("estado", estado)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOwnProfile(
        nombres: String,
        apellidos: String,
        telefono: String?
    ): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("No hay una sesión activa"))

        return try {
            firestore.collection("usuarios")
                .document(uid)
                .update(
                    mapOf(
                        "nombres" to normalizeSpaces(nombres),
                        "apellidos" to normalizeSpaces(apellidos),
                        "telefono" to telefono?.trim()?.takeIf { it.isNotBlank() }
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun normalizeSpaces(value: String): String {
        return value.trim().replace("\\s+".toRegex(), " ")
    }
}

package com.sufibra.network.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sufibra.network.domain.model.User
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

            // Obtener configuración del proyecto actual
            val primaryApp = FirebaseApp.getInstance()
            val options = primaryApp.options

            // Crear instancia secundaria
            val secondaryApp = FirebaseApp.initializeApp(
                context,
                options,
                "SecondaryApp"
            )!!

            val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

            // Crear usuario usando instancia secundaria
            val authResult = secondaryAuth
                .createUserWithEmailAndPassword(correo, password)
                .await()

            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("No se pudo crear el usuario"))

            val user = User(
                idUsuario = uid,
                nombres = nombres,
                apellidos = apellidos,
                correo = correo,
                rol = rol,
                estado = true,
                fechaCreacion = System.currentTimeMillis(),
                telefono = telefono,
                zonaAsignada = zonaAsignada,
                disponible = if (rol == "TECHNICIAN") true else null
            )

            firestore.collection("usuarios")
                .document(uid)
                .set(user)
                .await()

            // Cerrar sesión secundaria
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

            firestore.collection("usuarios")
                .document(user.idUsuario)
                .set(user)
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




}

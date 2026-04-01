package com.sufibra.network.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository {

    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadClientFacadePhoto(context: Context, uri: Uri): Result<String> {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mimeType)
                ?: "jpg"

            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Result.failure(Exception("Debes iniciar sesión para subir una foto."))

            val fileName = "client-facades/$userId/${System.currentTimeMillis()}-${UUID.randomUUID()}.$extension"
            val storageRef = storage.reference.child(fileName)
            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType ?: "image/jpeg")
                .build()

            contentResolver.openInputStream(uri)?.use { inputStream ->
                storageRef.putStream(inputStream, metadata).await()
            } ?: return Result.failure(Exception("No se pudo leer la imagen seleccionada."))

            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

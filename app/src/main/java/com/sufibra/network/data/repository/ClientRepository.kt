package com.sufibra.network.data.repository

import com.google.android.gms.common.api.Api
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.sufibra.network.domain.model.Client

class ClientRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val clientsCollection = firestore.collection("clients")

    suspend fun getAllClients(): Result<List<Client>> {
        return try {
            val snapshot = clientsCollection.get().await()
            val clients = snapshot.toObjects(Client::class.java)
            Result.success(clients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClientById(clientId: String): Result<Client> {
        return try {
            val document = clientsCollection.document(clientId).get().await()

            val client = document.toObject(Client::class.java)

            if (client != null) {
                Result.success(client.copy(idCliente = document.id))
            } else {
                Result.failure(Exception("Cliente no encontrado"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.sufibra.network.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sufibra.network.domain.model.Client
import kotlinx.coroutines.tasks.await

class ClientRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val clientsCollection = firestore.collection("clients")

    private suspend fun isDuplicateDni(dni: String, excludeClientId: String? = null): Boolean {
        val snapshot = clientsCollection
            .whereEqualTo("dni", dni)
            .get()
            .await()

        return snapshot.documents.any { document ->
            document.id != excludeClientId
        }
    }

    suspend fun getAllClients(): Result<List<Client>> {
        return try {
            val snapshot = clientsCollection.get().await()
            val clients = snapshot.documents
                .mapNotNull { document ->
                    document.toObject(Client::class.java)?.copy(idCliente = document.id)
                }
                .sortedBy { it.nombresApellidos.lowercase() }
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

    suspend fun createClient(client: Client): Result<Unit> {
        return try {
            val normalizedClient = normalizeClient(client)

            if (isDuplicateDni(normalizedClient.dni)) {
                return Result.failure(Exception("Ya existe un cliente registrado con ese DNI."))
            }

            val document = clientsCollection.document()
            val clientToSave = normalizedClient.copy(
                idCliente = document.id,
                estadoCliente = true,
                fechaRegistro = System.currentTimeMillis()
            )
            document.set(clientToSave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateClient(client: Client): Result<Unit> {
        return try {
            val normalizedClient = normalizeClient(client)

            if (isDuplicateDni(normalizedClient.dni, normalizedClient.idCliente)) {
                return Result.failure(Exception("Ya existe un cliente registrado con ese DNI."))
            }

            clientsCollection
                .document(normalizedClient.idCliente)
                .set(normalizedClient)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateClientStatus(clientId: String, estadoCliente: Boolean): Result<Unit> {
        return try {
            clientsCollection
                .document(clientId)
                .update("estadoCliente", estadoCliente)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun normalizeClient(client: Client): Client {
        return client.copy(
            nombresApellidos = normalizeSpaces(client.nombresApellidos),
            dni = client.dni.trim(),
            celular = client.celular.trim(),
            direccion = normalizeSpaces(client.direccion),
            referencia = normalizeSpaces(client.referencia),
            zona = normalizeSpaces(client.zona),
            cajaNAP = normalizeSpaces(client.cajaNAP),
            puertoNAP = normalizeSpaces(client.puertoNAP),
            linkMaps = client.linkMaps.trim(),
            fotoFachada = client.fotoFachada.trim()
        )
    }

    private fun normalizeSpaces(value: String): String {
        return value.trim().replace("\\s+".toRegex(), " ")
    }
}

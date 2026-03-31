package com.sufibra.network.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sufibra.network.domain.model.Client
import com.sufibra.network.domain.model.Event
import kotlinx.coroutines.tasks.await

class EventRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val eventsCollection = firestore.collection("events")
    private val usersCollection = firestore.collection("usuarios")

    suspend fun createEvent(event: Event): Result<Unit> {
        return try {
            val docRef = eventsCollection.document()
            val newEvent = event.copy(idEvento = docRef.id)
            docRef.set(newEvent).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .orderBy("fechaCreacion", Query.Direction.DESCENDING)
                .get()
                .await()
            val events = snapshot.toObjects(Event::class.java)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAvailableEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .whereEqualTo("estadoEvento", "DISPONIBLE")
                .get()
                .await()

            val events = snapshot.toObjects(Event::class.java)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveEventForTechnician(technicianId: String): Result<Event?> {
        return try {
            val snapshot = eventsCollection
                .whereEqualTo("tecnicoId", technicianId)
                .whereIn("estadoEvento", listOf("TOMADO", "EN PROCESO"))
                .limit(1)
                .get()
                .await()

            val event = snapshot.toObjects(Event::class.java).firstOrNull()
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFinishedEventsForTechnician(technicianId: String): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .whereEqualTo("tecnicoId", technicianId)
                .whereEqualTo("estadoEvento", "FINALIZADO")
                .get()
                .await()

            val events = snapshot.toObjects(Event::class.java)
                .sortedByDescending { it.fechaFinalizacion ?: it.fechaCreacion }

            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun takeEvent(eventId: String, technicianId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val technicianRef = usersCollection.document(technicianId)
                val eventSnapshot = transaction.get(eventRef)
                val technicianSnapshot = transaction.get(technicianRef)
                val currentState = eventSnapshot.getString("estadoEvento")
                val currentTechnicianId = eventSnapshot.getString("tecnicoId")
                val technicianAvailable = technicianSnapshot.getBoolean("disponible")

                if (!eventSnapshot.exists()) {
                    throw IllegalStateException("El evento ya no existe.")
                }

                if (!technicianSnapshot.exists()) {
                    throw IllegalStateException("No se encontró la ficha del técnico.")
                }

                if (currentState == "CANCELADO") {
                    throw IllegalStateException("Este evento fue cancelado y ya no puede tomarse.")
                }

                if (currentState != "DISPONIBLE") {
                    throw IllegalStateException("Este evento ya no está disponible para ser tomado.")
                }

                if (!currentTechnicianId.isNullOrBlank()) {
                    throw IllegalStateException("Este evento ya fue asignado a otro técnico.")
                }

                if (technicianAvailable == false) {
                    throw IllegalStateException("No puedes tomar este evento porque ya tienes uno activo.")
                }

                transaction.update(
                    eventRef,
                    mapOf(
                        "estadoEvento" to "TOMADO",
                        "tecnicoId" to technicianId,
                        "fechaToma" to System.currentTimeMillis()
                    )
                )
                transaction.update(
                    technicianRef,
                    mapOf("disponible" to false)
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startEvent(
        eventId: String,
        technicianId: String
    ): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val technicianRef = usersCollection.document(technicianId)
                val eventSnapshot = transaction.get(eventRef)
                val technicianSnapshot = transaction.get(technicianRef)
                val currentState = eventSnapshot.getString("estadoEvento")
                val assignedTechnicianId = eventSnapshot.getString("tecnicoId")

                if (!eventSnapshot.exists()) {
                    throw IllegalStateException("El evento ya no existe.")
                }

                if (!technicianSnapshot.exists()) {
                    throw IllegalStateException("No se encontró la ficha del técnico.")
                }

                if (currentState == "CANCELADO") {
                    throw IllegalStateException("Este evento fue cancelado y ya no puede iniciarse.")
                }

                if (currentState != "TOMADO") {
                    throw IllegalStateException("Solo puedes iniciar eventos en estado TOMADO.")
                }

                if (assignedTechnicianId != technicianId) {
                    throw IllegalStateException("No puedes iniciar un evento que no te pertenece.")
                }

                transaction.update(
                    eventRef,
                    mapOf(
                        "estadoEvento" to "EN PROCESO",
                        "fechaInicio" to System.currentTimeMillis()
                    )
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getEventById(eventId: String): Result<Event> {
        return try {
            val snapshot = eventsCollection
                .document(eventId)
                .get()
                .await()

            val event = snapshot.toObject(Event::class.java)

            if (event != null) {
                Result.success(event)
            } else {
                Result.failure(Exception("Evento no encontrado"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finalizeEvent(
        eventId: String,
        technicianId: String,
        solucionAplicada: String,
        observaciones: String?
    ): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val technicianRef = usersCollection.document(technicianId)
                val eventSnapshot = transaction.get(eventRef)
                val technicianSnapshot = transaction.get(technicianRef)
                val currentState = eventSnapshot.getString("estadoEvento")
                val assignedTechnicianId = eventSnapshot.getString("tecnicoId")

                if (!eventSnapshot.exists()) {
                    throw IllegalStateException("El evento ya no existe.")
                }

                if (!technicianSnapshot.exists()) {
                    throw IllegalStateException("No se encontró la ficha del técnico.")
                }

                if (currentState == "CANCELADO") {
                    throw IllegalStateException("Este evento fue cancelado y ya no puede finalizarse.")
                }

                if (currentState != "EN PROCESO") {
                    throw IllegalStateException("Solo puedes finalizar eventos en estado EN PROCESO.")
                }

                if (assignedTechnicianId != technicianId) {
                    throw IllegalStateException("No puedes finalizar un evento que no te pertenece.")
                }

                transaction.update(
                    eventRef,
                    mapOf(
                        "estadoEvento" to "FINALIZADO",
                        "fechaFinalizacion" to System.currentTimeMillis(),
                        "solucionAplicada" to solucionAplicada,
                        "observaciones" to observaciones
                    )
                )
                transaction.update(
                    technicianRef,
                    mapOf("disponible" to true)
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finalizeInstallationWithClient(
        eventId: String,
        technicianId: String,
        solucionAplicada: String,
        observaciones: String?,
        client: Client
    ): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val clientDoc = firestore.collection("clients").document()
            val clientToSave = client.copy(
                idCliente = clientDoc.id,
                estadoCliente = true,
                fechaRegistro = now
            )

            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val technicianRef = usersCollection.document(technicianId)
                val eventSnapshot = transaction.get(eventRef)
                val technicianSnapshot = transaction.get(technicianRef)
                val currentState = eventSnapshot.getString("estadoEvento")
                val assignedTechnicianId = eventSnapshot.getString("tecnicoId")

                if (!eventSnapshot.exists()) {
                    throw IllegalStateException("El evento ya no existe.")
                }

                if (!technicianSnapshot.exists()) {
                    throw IllegalStateException("No se encontró la ficha del técnico.")
                }

                if (currentState == "CANCELADO") {
                    throw IllegalStateException("Este evento fue cancelado y ya no puede finalizarse.")
                }

                if (currentState != "EN PROCESO") {
                    throw IllegalStateException("Solo puedes finalizar instalaciones en estado EN PROCESO.")
                }

                if (assignedTechnicianId != technicianId) {
                    throw IllegalStateException("No puedes finalizar una instalación que no te pertenece.")
                }

                transaction.set(clientDoc, clientToSave)
                transaction.update(
                    eventRef,
                    mapOf(
                        "estadoEvento" to "FINALIZADO",
                        "fechaFinalizacion" to now,
                        "solucionAplicada" to solucionAplicada,
                        "observaciones" to observaciones,
                        "clienteId" to clientDoc.id
                    )
                )
                transaction.update(
                    technicianRef,
                    mapOf("disponible" to true)
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEventEditableFields(
        eventId: String,
        descripcion: String,
        prioridad: String
    ): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val snapshot = transaction.get(eventRef)
                val currentState = snapshot.getString("estadoEvento")

                if (currentState != "DISPONIBLE") {
                    throw IllegalStateException("Solo se pueden editar eventos en estado DISPONIBLE")
                }

                transaction.update(
                    eventRef,
                    mapOf(
                        "descripcion" to descripcion,
                        "prioridad" to prioridad
                    )
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelEvent(
        eventId: String,
        canceladoPor: String
    ): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val snapshot = transaction.get(eventRef)
                val currentState = snapshot.getString("estadoEvento")
                val technicianId = snapshot.getString("tecnicoId")

                if (currentState !in listOf("DISPONIBLE", "TOMADO")) {
                    throw IllegalStateException("Solo se pueden cancelar eventos en estado DISPONIBLE o TOMADO")
                }

                transaction.update(
                    eventRef,
                    mapOf(
                        "estadoEvento" to "CANCELADO",
                        "fechaCancelacion" to System.currentTimeMillis(),
                        "canceladoPor" to canceladoPor
                    )
                )

                if (currentState == "TOMADO" && !technicianId.isNullOrBlank()) {
                    transaction.update(
                        usersCollection.document(technicianId),
                        mapOf("disponible" to true)
                    )
                }
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun releaseTakenEvent(
        eventId: String,
        liberadoPor: String
    ): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val snapshot = transaction.get(eventRef)
                val currentState = snapshot.getString("estadoEvento")
                val technicianId = snapshot.getString("tecnicoId")

                if (currentState != "TOMADO") {
                    throw IllegalStateException("Solo se pueden liberar eventos en estado TOMADO")
                }

                if (technicianId.isNullOrBlank()) {
                    throw IllegalStateException("No hay un técnico asignado para liberar este evento.")
                }

                transaction.update(
                    eventRef,
                    mapOf(
                        "estadoEvento" to "DISPONIBLE",
                        "tecnicoId" to null,
                        "fechaToma" to null,
                        "fechaLiberacion" to System.currentTimeMillis(),
                        "liberadoPor" to liberadoPor
                    )
                )

                transaction.update(
                    usersCollection.document(technicianId),
                    mapOf("disponible" to true)
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

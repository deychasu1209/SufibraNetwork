package com.sufibra.network.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.sufibra.network.domain.model.Event
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

class EventRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val eventsCollection = firestore.collection("events")

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

    suspend fun takeEvent(eventId: String, technicianId: String): Result<Unit> {
        return try {

            eventsCollection.document(eventId).update(
                mapOf(
                    "estadoEvento" to "TOMADO",
                    "tecnicoId" to technicianId,
                    "fechaToma" to System.currentTimeMillis()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startEvent(eventId: String): Result<Unit> {
        return try {
            eventsCollection.document(eventId).update(
                mapOf(
                    "estadoEvento" to "EN PROCESO",
                    "fechaInicio" to System.currentTimeMillis()
                )
            ).await()

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
        solucionAplicada: String,
        observaciones: String?
    ): Result<Unit> {
        return try {
            eventsCollection.document(eventId).update(
                mapOf(
                    "estadoEvento" to "FINALIZADO",
                    "fechaFinalizacion" to System.currentTimeMillis(),
                    "solucionAplicada" to solucionAplicada,
                    "observaciones" to observaciones
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
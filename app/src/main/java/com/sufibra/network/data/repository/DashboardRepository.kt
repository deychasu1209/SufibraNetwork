package com.sufibra.network.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sufibra.network.domain.model.AdminDashboardMetrics
import kotlinx.coroutines.tasks.await

class DashboardRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAdminMetrics(): Result<AdminDashboardMetrics> {
        return try {
            val eventsSnapshot = firestore.collection("events").get().await()

            val metrics = AdminDashboardMetrics(
                totalEventos = eventsSnapshot.size(),
                disponibles = eventsSnapshot.documents.count {
                    it.getString("estadoEvento") == "DISPONIBLE"
                },
                tomados = eventsSnapshot.documents.count {
                    it.getString("estadoEvento") == "TOMADO"
                },
                enProceso = eventsSnapshot.documents.count {
                    it.getString("estadoEvento") == "EN PROCESO"
                },
                finalizados = eventsSnapshot.documents.count {
                    it.getString("estadoEvento") == "FINALIZADO"
                },
                cancelados = eventsSnapshot.documents.count {
                    it.getString("estadoEvento") == "CANCELADO"
                }
            )

            Result.success(metrics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

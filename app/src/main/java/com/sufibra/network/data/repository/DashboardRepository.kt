package com.sufibra.network.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sufibra.network.domain.model.AdminDashboardMetrics
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class DashboardRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAdminMetrics(): Result<AdminDashboardMetrics> {
        return try {
            coroutineScope {
                val eventsDeferred = async { firestore.collection("events").get().await() }
                val usersDeferred = async { firestore.collection("usuarios").get().await() }
                val clientsDeferred = async { firestore.collection("clients").get().await() }

                val eventsSnapshot = eventsDeferred.await()
                val usersSnapshot = usersDeferred.await()
                val clientsSnapshot = clientsDeferred.await()

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
                    },
                    totalUsuarios = usersSnapshot.size(),
                    totalClientes = clientsSnapshot.size()
                )

                Result.success(metrics)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

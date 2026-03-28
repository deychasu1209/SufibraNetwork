package com.sufibra.network.domain.model

data class AdminDashboardMetrics(
    val totalEventos: Int = 0,
    val disponibles: Int = 0,
    val tomados: Int = 0,
    val enProceso: Int = 0,
    val finalizados: Int = 0,
    val cancelados: Int = 0,
    val totalUsuarios: Int = 0,
    val totalClientes: Int = 0
)

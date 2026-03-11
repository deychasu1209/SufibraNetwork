package com.sufibra.network.domain.model

data class User(
    val idUsuario: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val rol: String = "",           // "ADMIN" o "TECHNICIAN"
    val estado: Boolean = true,
    val fechaCreacion: Long = 0L,

    // Campos específicos de Técnico (opcionales)
    val telefono: String? = null,
    val zonaAsignada: String? = null,
    val disponible: Boolean? = null
)

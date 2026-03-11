package com.sufibra.network.domain.model

data class Event(
    val idEvento: String = "",
    val tipoEvento: String = "",
    val descripcion: String = "",
    val estadoEvento: String = "",
    val prioridad: String = "",

    val fechaCreacion: Long = 0L,
    val fechaToma: Long? = null,
    val fechaInicio: Long? = null,
    val fechaFinalizacion: Long? = null,

    val solucionAplicada: String? = null,
    val observaciones: String? = null,

    val clienteId: String? = null,
    val tecnicoId: String? = null,
    val administradorId: String? = null
)
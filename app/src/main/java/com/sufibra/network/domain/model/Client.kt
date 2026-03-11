package com.sufibra.network.domain.model

data class Client(
    val idCliente: String = "",
    val nombresApellidos: String = "",
    val dni: String = "",
    val celular: String = "",
    val zona: String = "",
    val direccion: String = "",
    val referencia: String = "",
    val linkMaps: String = "",
    val fotoFachada: String = "",
    val cajaNAP: String = "",
    val puertoNAP: String = "",
    val estadoCliente: Boolean = true
)
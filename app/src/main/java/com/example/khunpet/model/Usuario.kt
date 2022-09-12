package com.example.khunpet.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    var apellido: String? = "",
    var direccion: String? = "",
    var nombre: String? = "",
    var numero: String? = "",
    var guid: String? = "",
    var tipo: String? = ""
)
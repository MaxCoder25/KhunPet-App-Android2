package com.example.khunpet.model

import kotlinx.serialization.Serializable

@Serializable
data class PublicationTime (

    var tipo: String? = "",
    var nombre: String? = "",
    var edad: String? = "",
    var genero: String? = "",
    var color: String? = "",
    var tamano: String? = "",
    var vacunado: String? = "",
    var desparazitado: String? = "",
    var esterilizado: String? = "",
    var condicion: String? = "",
    var descripcion: String? = "",
    var foto: String? = ""
)
package com.example.khunpet.model
import kotlinx.serialization.Serializable

@Serializable
data class Publication(
    var locacion: String? = "",
    var telefono: String? = "",
    var descripcion: String? = "",
    var tipo: String? = "",
    var condicion: String? = "",
    var fecha: String? = "",
    var foto: String? = ""
)

package com.example.khunpet.model
import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Publication(
    @DocumentId
    var documentId: String = "",
    var locacion: String? = "",
    var telefono: String? = "",
    var descripcion: String? = "",
    var tipo: String? = "",
    var condicion: String? = "",
    var fecha: String? = "",
    var foto: String? = "",
    var user: String? = "",
)

package com.example.khunpet.model

import kotlinx.serialization.Serializable

@Serializable
data class AdoptionPublication (

    var Type: String? = "",
    var Name : String? = "",
    var Age  : String? = "",
    var Breed1: String? = "",
    var Breed2  : String? = "",
    var Gender : String? = "",
    var Color1: String? = "",
    var Color2: String? = "",
    var Color3: String? = "",

    var MaturitySize : String? = "",
    var FurLength : String? = "",
    var Vaccinated : String? = "",
    var Dewormed : String? = "",
    var Sterilized : String? = "",
    var Health : String? = "",

    var Description: String? = "",

    var Quantity:String? = "",
    var Fee: String? = "",
    var State: String? = "",
    var VideoAmt: String? = "",

    var PetID: String? = "",
    var RescuerID: String? = "",
    var PhotoAmt: String? = "",
    var AdoptionSpeed: String? = "",
    var publicationDate_Mascota: String? = "",
    //var AdoptionDate_Mascota: String? = "",
    var ImageUrl: String? = "",
    var telefono: String? = "",
    var refugio: String? = ""


)
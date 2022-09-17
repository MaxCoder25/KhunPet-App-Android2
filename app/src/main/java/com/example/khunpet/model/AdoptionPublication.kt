package com.example.khunpet.model

import android.os.Parcel
import android.os.Parcelable
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


) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Type)
        parcel.writeString(Name)
        parcel.writeString(Age)
        parcel.writeString(Breed1)
        parcel.writeString(Breed2)
        parcel.writeString(Gender)
        parcel.writeString(Color1)
        parcel.writeString(Color2)
        parcel.writeString(Color3)
        parcel.writeString(MaturitySize)
        parcel.writeString(FurLength)
        parcel.writeString(Vaccinated)
        parcel.writeString(Dewormed)
        parcel.writeString(Sterilized)
        parcel.writeString(Health)
        parcel.writeString(Description)
        parcel.writeString(Quantity)
        parcel.writeString(Fee)
        parcel.writeString(State)
        parcel.writeString(VideoAmt)
        parcel.writeString(PetID)
        parcel.writeString(RescuerID)
        parcel.writeString(PhotoAmt)
        parcel.writeString(AdoptionSpeed)
        parcel.writeString(publicationDate_Mascota)
        parcel.writeString(ImageUrl)
        parcel.writeString(telefono)
        parcel.writeString(refugio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdoptionPublication> {
        override fun createFromParcel(parcel: Parcel): AdoptionPublication {
            return AdoptionPublication(parcel)
        }

        override fun newArray(size: Int): Array<AdoptionPublication?> {
            return arrayOfNulls(size)
        }
    }
}
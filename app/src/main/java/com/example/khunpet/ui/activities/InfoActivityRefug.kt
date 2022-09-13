package com.example.khunpet.ui.activities


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.InfoViewModelRefug
import com.example.khunpet.databinding.ActivityInfoRefugBinding
import com.example.khunpet.model.AdoptionPublication
import com.example.khunpet.utils.AppDatabase
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*


class InfoActivityRefug : AppCompatActivity() {

    private lateinit var binding : ActivityInfoRefugBinding
    val viewModel : InfoViewModelRefug by viewModels()
    var storage = AppDatabase.getStorageReference()
    private lateinit var phoneNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoRefugBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val pub : String? = intent.getStringExtra("publication")
        val gson = Gson()
        viewModel.publication.postValue(gson.fromJson(pub,AdoptionPublication::class.java))

        viewModel.publication.observe(this) {
            loadInformation(it)
        }


        viewModel.loading.observe(this) { bool ->
            if (bool) {
                binding.progressBar2.visibility = View.VISIBLE
            } else {
                binding.progressBar2.visibility = View.GONE
            }
        }


      /*  binding.wspButton.setOnClickListener {
            val numberLocal : String? = viewModel.publication.value!!.telefono
            val numberInternational : String = "593" + numberLocal!!.subSequence(1, numberLocal.length)
            phoneNumber = numberInternational
            openWhatsApp(numberInternational)
        }

        binding.callButton.setOnClickListener {
            val numberLocal : String? = viewModel.publication.value!!.telefono
            val numberInternational : String = "593" + numberLocal!!.subSequence(1, numberLocal.length)
            phoneNumber = numberInternational
            onCall(numberInternational)
        }
*/


        binding.btnPredecir.setOnClickListener {
           // viewModel.publication.observe(this) {

            predecirFuncionality(this)

          //  binding.TiempoAdopcion.text = petIDAux
           // }

        }



    }


    fun predecirFuncionality(view: InfoActivityRefug) {

        GlobalScope.launch(Dispatchers.Main){ // launches coroutine in main thread
            updateUi()

            delay(8000L)
            binding.TiempoAdopcion.text = getStringSharedPreference_petAUX()

        }

        //updateUi()

    }

      fun updateUi(){
        val value = GlobalScope.async { // creates worker thread
           withContext(Dispatchers.Default) {

             //  viewModel.publication.observe(this) {




               var petID =getStringSharedPreference_petID()


               if (petID != null) {
                   viewModel.makeRequest(petID)


                   var petIDAux = ""

                   when (getStringSharedPreference_petIDResponse()) {
                       "0" ->  {
                           petIDAux = "El mismo día"
                       }
                       "1" -> {
                           petIDAux = "Entre 1 y 7 días"
                       }
                       "2" -> {
                           petIDAux = "Entre 8 y 30 días"
                       }
                       "3" -> {
                           petIDAux = "Entre 31 y 90 días"
                       }
                       "4" -> {
                           petIDAux = "Más de 100 días"
                       }
                       else -> {
                          // petIDAux = "Faltan archivos de analisis de esta mascota, intente de nuevo"
                           petIDAux = "..."

                       }
                   }

                   saveSharedPreferencepetAUX(petIDAux)


               }
        }

           // binding.TiempoAdopcion.text = getStringSharedPreference_petAUX()

    }
         }




    fun getStringSharedPreference_petIDResponse(): String? {
        var editorSP = AppDatabase.getShareDB()
        var petID = editorSP.getString("petIDResponse", "2")

        return petID

    }

    fun saveSharedPreferencepetID(petID: String) {
        var editorSP = AppDatabase.getShareDB().edit()
        editorSP.putString ("petID", petID )
        editorSP.commit()
    }



  fun getStringSharedPreference_petID(): String? {
      var editorSP = AppDatabase.getShareDB()
      var petID = editorSP.getString("petID", "null")

      return petID

  }

    fun saveSharedPreferencepetAUX(petAUX: String) {
        var editorSP = AppDatabase.getShareDB().edit()
        editorSP.putString ("petAUX", petAUX )
        editorSP.commit()
    }



    fun getStringSharedPreference_petAUX(): String? {
        var editorSP = AppDatabase.getShareDB()
        var petID = editorSP.getString("petAUX", "null")

        return petID

    }

    private fun loadInformation(publication: AdoptionPublication) {
        storage.getReference("lost/images").child(publication.ImageUrl!!).downloadUrl
            .addOnSuccessListener {

                saveSharedPreferencepetID(publication.PetID!!)


                binding.infoNombre.text = publication.Name

                var BreedAux = "Mestizo"

                when (publication.Breed1) {
                    "69" ->  {
                        BreedAux = "Cocker Spaniel"
                    }
                    "169" -> {
                        BreedAux = "Pequines"
                    }
                    "215" -> {
                        BreedAux = "Poodle"
                    }
                    "76" -> {
                        BreedAux = "Dalmata"
                    }
                    "103" -> {
                        BreedAux = "Pastor Alemán"
                    }
                    "195" -> {
                        BreedAux = "Schnauzer"
                    }
                    "60" -> {
                        BreedAux = "Chihuahua"
                    }
                    "44" -> {
                        BreedAux = "Boxer"
                    }
                    "65" -> {
                        BreedAux = "Chow Chow"
                    }
                    "141" -> {
                        BreedAux = "Labrador Retriever"
                    }
                    "307" -> {
                        BreedAux = "Mestizo"
                    }




        //if (Type==2){
         //   when (Breed1) {

                "292" -> {
                    BreedAux = "Siames"
                }
                "292"-> {
                    BreedAux = "Mestizo"
                    }
                "285" -> {
                    BreedAux = "Persa"
                }
                "304" -> {
                    BreedAux = "Angora"
                }
                "250" -> {
                    BreedAux = "Bombay"
                }
                "288" -> {
                    BreedAux = "Ragdoll"
                }
                "251" -> {
                    BreedAux = "British Shorthair"
                }

                }

                binding.infoRaza.text = BreedAux

     //        MaturitySize

                var MaturitySizeAux = "Pequeño"

                when (publication.MaturitySize) {
                    "1" ->  {
                        MaturitySizeAux = "Pequeño"
                    }
                    "2" -> {
                        MaturitySizeAux = "Mediano"
                    }
                    "3" -> {
                        MaturitySizeAux = "Grande"
                    }
                    "4" -> {
                        MaturitySizeAux = "Extra grande"
                    }
                    "0" -> {
                        MaturitySizeAux = "No especificado"
                    }
                }

                binding.infoTamano.text = MaturitySizeAux


              /*  var HealthAux = "Saludable"
                when (publication.Health) {
                    "1" ->  {
                        HealthAux = "Saludable"
                    }
                    "2" -> {
                        HealthAux = "Grave"
                    }
                    "3" -> {
                        HealthAux = "Critico"
                    }
                    "0" -> {
                        HealthAux = "No especificado"
                    }
                }
*/

                var GenderAux = "1"
                when (publication.Gender) {
                    "1" ->  {
                        GenderAux = "Macho"
                    }
                    "2" -> {
                        GenderAux = "Hembra"
                    }
                    "3" -> {
                        GenderAux = "No especificado"
                    }

                }


                binding.infoGenero.text = GenderAux

                binding.infoRefugio.text = publication.refugio

                binding.infoFecha.text = publication.publicationDate_Mascota
                binding.infoDescripcion.text = publication.Description

                Picasso.get()
                    .load(it.toString())
                    .fit()
                    .placeholder(R.drawable.place_holder)
                    .into(binding.publicacionImv)
            }
    }

    private fun openWhatsApp(num: String) {
        val isAppInstalled = appInstalledOrNot("com.whatsapp")
        if (isAppInstalled) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$num"))
            startActivity(intent)
        } else {
            Toast.makeText(this,"WhatsApp no instalado",Toast.LENGTH_SHORT).show()
        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = this.packageManager
        return try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun onCall(number: String) {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CALL_PHONE),
                123
            )
        } else {
            startActivity(Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:$number")))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            123 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCall(phoneNumber)
            } else {
                Log.d("TAG", "Call Permission Not Granted")
            }
            else -> {}
        }
    }
}
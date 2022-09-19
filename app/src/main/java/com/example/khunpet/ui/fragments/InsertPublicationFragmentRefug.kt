package com.example.khunpet.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.InsertPublicationRefugViewModel
import com.example.khunpet.databinding.ActivityMainRefugBinding
import com.example.khunpet.databinding.FragmentPublicationRefugBinding
import com.example.khunpet.model.AdoptionPublication
import com.example.khunpet.ui.activities.MainActivityRefug
import com.example.khunpet.ui.activities.MainActivityVision
import com.example.khunpet.utils.AppDatabase
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.squareup.picasso.Picasso
import java.util.*

class InsertPublicationFragmentRefug : Fragment() {

    private val viewModel : InsertPublicationRefugViewModel by viewModels()
    private var _binding: FragmentPublicationRefugBinding? = null
    private val binding get() = _binding!!

    private lateinit var binding2 : ActivityMainRefugBinding

    private lateinit var selectImg: ActivityResultLauncher<Intent>


    override fun onResume() {
        super.onResume()

        val animals = resources.getStringArray(R.array.animal_type)
        val arrayAdapterA = ArrayAdapter(requireContext(), R.layout.dropdown_item, animals)
        binding.autoCompleteAnimalType.setAdapter(arrayAdapterA)

        val genders = resources.getStringArray(R.array.animal_gender)
        val arrayAdapterG = ArrayAdapter(requireContext(), R.layout.dropdown_item, genders)
        binding.autoCompleteGeneroType.setAdapter(arrayAdapterG)

        if(animals.get(0)=="Perro"){
        val breeds = resources.getStringArray(R.array.animal_breed_dog)
        val arrayAdapterG = ArrayAdapter(requireContext(), R.layout.dropdown_item, breeds)
        binding.autoCompletebreedType.setAdapter(arrayAdapterG)
        }


        if(animals.get(0)=="Gato"){
            val breeds = resources.getStringArray(R.array.animal_breed_cat)
            val arrayAdapterG = ArrayAdapter(requireContext(), R.layout.dropdown_item, breeds)
            binding.autoCompletebreedType.setAdapter(arrayAdapterG)
        }




        val colors = resources.getStringArray(R.array.animal_color)
        val arrayAdapterC = ArrayAdapter(requireContext(), R.layout.dropdown_item, colors)
        binding.autoCompleteColorType.setAdapter(arrayAdapterC)

        binding.autoCompleteColor2Type.setAdapter(arrayAdapterC)


        val sizes = resources.getStringArray(R.array.animal_size)
        val arrayAdapterS = ArrayAdapter(requireContext(), R.layout.dropdown_item, sizes)
        binding.autoCompleteSizeType.setAdapter(arrayAdapterS)

        val vacinated = resources.getStringArray(R.array.animal_vacinate)
        val arrayAdapterV = ArrayAdapter(requireContext(), R.layout.dropdown_item, vacinated)
        binding.autoCompleteVacinatedType.setAdapter(arrayAdapterV)

        val dewormed = resources.getStringArray(R.array.animal_dewormed)
        val arrayAdapterD = ArrayAdapter(requireContext(), R.layout.dropdown_item, dewormed)
        binding.autoCompleteDewormedType.setAdapter(arrayAdapterD)

        val sterilized = resources.getStringArray(R.array.animal_sterilized)
        val arrayAdapterSt = ArrayAdapter(requireContext(), R.layout.dropdown_item, sterilized)
        binding.autoCompleteSterilizedType.setAdapter(arrayAdapterSt)

        val health = resources.getStringArray(R.array.animal_health)
        val arrayAdapterH = ArrayAdapter(requireContext(), R.layout.dropdown_item, health)
        binding.autoCompleteHealthType.setAdapter(arrayAdapterH)

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentPublicationRefugBinding.inflate(inflater, container, false)

        selectImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                viewModel.imageUri.postValue(uri)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*  binding.imageButton.setOnClickListener {
            ImagePicker.with(requireActivity())
                .provider(ImageProvider.BOTH)
                .crop(224f,224f)
                .createIntentFromDialog { selectImg.launch(it) }
        }
*/
        binding.publicarButton.setOnClickListener {
            buildPublication()

            clear()

        }

        viewModel.imageUri.observe(viewLifecycleOwner) {
          //  loadWithPicasso(it)
        }

        viewModel.loading.observe(viewLifecycleOwner) { bool ->
            if (bool) {
                binding.loadingPublicacion.visibility = View.VISIBLE
            } else {
                binding.loadingPublicacion.visibility = View.GONE
            }
        }


        binding.autoCompleteAnimalType.setOnItemClickListener { _, _, position, _ ->
            // You can get the label or item that the user clicked:

          //  val value = adapter.getItem(position) ?: ""
           // Toast.makeText(this, value, Toast.LENGTH_LONG).show();

         //   Toast.makeText(requireContext(),"$position selected",Toast.LENGTH_LONG).show()

            if(position==0){
                val breeds = resources.getStringArray(R.array.animal_breed_dog)
                val arrayAdapterG = ArrayAdapter(requireContext(), R.layout.dropdown_item, breeds)
                binding.autoCompletebreedType.setAdapter(arrayAdapterG)
            }
            if(position==1){
                val breeds = resources.getStringArray(R.array.animal_breed_cat)
                val arrayAdapterG = ArrayAdapter(requireContext(), R.layout.dropdown_item, breeds)
                binding.autoCompletebreedType.setAdapter(arrayAdapterG)
            }

        }

    }

    private fun buildPublication() {

       /* if (viewModel.imageUri.value == Uri.EMPTY) {
            Toast.makeText(requireContext(), "Es necesario una imagen", Toast.LENGTH_SHORT).show()
            return
        }
*/

        var Type = 0

        val TypeString = binding.autoCompleteAnimalType.text.toString()

        if(TypeString == "Perro"){
             Type = 1
        }else {
            if(TypeString == "Gato"){
             Type = 2
            }
        }


        val Name = binding.nombreEditText.text.toString()
        val Age = binding.edadEditText.text.toString()
        //109 = Golden Retriever

        val Breed1 = binding.autoCompletebreedType.text.toString()

        var BreedAux = "307"

         if (Type==1){


        when (Breed1) {
            "Cocker Spaniel" ->  {
                BreedAux = "69"
            }
            "Pequines" -> {
                BreedAux = "169"
            }
            "Poodle" -> {
                BreedAux = "215"
            }
            "Dalmata" -> {
                BreedAux = "76"
            }
            "Pastor Alemán" -> {
                BreedAux = "103"
            }
            "Schnauzer" -> {
                BreedAux = "195"
            }
            "Chihuahua" -> {
                BreedAux = "60"
            }
            "Boxer" -> {
                BreedAux = "44"
            }
            "Chow Chow" -> {
                BreedAux = "65"
            }
            "Labrador Retriever" -> {
                BreedAux = "141"
            }
            "Mestizo" -> {
                BreedAux = "307"
            }

             }
         }

             if (Type==2){
           when (Breed1) {

                 "Siames", "Mestizo " -> {
                BreedAux = "292"
            }
            "Persa" -> {
                BreedAux = "285"
            }
            "Angora" -> {
                BreedAux = "304"
            }
            "Bombay" -> {
                BreedAux = "250"
            }
            "Ragdoll" -> {
                BreedAux = "288"
            }
            "British Shorthair" -> {
                BreedAux = "251"
            }
        }

             }

        val Breed2 = BreedAux

        val Gender = binding.autoCompleteGeneroType.text.toString()

        var GenderAux = "0"
        when (Gender) {
            "Macho" ->  {
                GenderAux = "1"
            }
            "Hembra" -> {
                GenderAux = "2"
            }
            else -> {
                GenderAux = "3"
            }

        }

        val Color1 = binding.autoCompleteColorType.text.toString()

        var Color1Aux = "0"
        when (Color1) {
            "Negro" ->  {
                Color1Aux = "1"
            }
            "Café" -> {
                Color1Aux = "2"
            }
            "Dorado" -> {
                Color1Aux = "3"
            }
            "Amarillo" -> {
                Color1Aux = "4"
            }
            "Crema" -> {
                Color1Aux = "5"
            }
            "Gris" -> {
                Color1Aux = "6"
            }
            "Blanco" -> {
                Color1Aux = "7"
            }
            else -> {
                Color1Aux = "1"
            }
        }


        val Color2 = binding.autoCompleteColorType.text.toString()

        var Color2Aux = "1"

        when (Color2) {
            "Negro" ->  {
                Color2Aux = "1"
            }
            "Café" -> {
                Color2Aux = "2"
            }
            "Dorado" -> {
                Color2Aux = "3"
            }
            "Amarillo" -> {
                Color2Aux = "4"
            }
            "Crema" -> {
                Color2Aux = "5"
            }
            "Gris" -> {
                Color2Aux = "6"
            }
            "Blanco" -> {
                Color2Aux = "7"
            }
            else -> {
                Color2Aux = "1"
            }
        }



        val Color3 ="1"
        val MaturitySize = binding.autoCompleteSizeType.text.toString()

        var MaturitySizeAux = "0"
        when (MaturitySize) {
            "Pequeño" ->  {
                MaturitySizeAux = "1"
            }
            "Mediano" -> {
                MaturitySizeAux = "2"
            }
            "Grande" -> {
                MaturitySizeAux = "3"
            }
            "Extra grande" -> {
                MaturitySizeAux = "4"
            }
            "No especificado" -> {
                MaturitySizeAux = "0"
            }
                       }

       val  FurLength = "2"
        val Vaccinated = binding.autoCompleteVacinatedType.text.toString()

        var VaccinatedAux = "0"
        when (Vaccinated) {
            "Si" ->  {
                VaccinatedAux = "1"
            }
            "No" -> {
                MaturitySizeAux = "2"
            }
            "Desconozco" -> {
                MaturitySizeAux = "3"
            }

            }

        val Dewormed = binding.autoCompleteDewormedType.text.toString()

        var DewormedAux = "0"
        when (Dewormed) {
            "Si" ->  {
                DewormedAux = "1"
            }
            "No" -> {
                DewormedAux = "2"
            }
            "Desconozco" -> {
                DewormedAux = "3"
            }

        }


        val Sterilized = binding.autoCompleteSterilizedType.text.toString()

        var SterilizedAux = "3"
        when (Sterilized) {
            "Si" ->  {
                SterilizedAux = "1"
            }
            "No" -> {
                SterilizedAux = "2"
            }
            "Desconozco" -> {
                SterilizedAux = "3"
            }

        }


        val Health = binding.autoCompleteHealthType.text.toString()

        var HealthAux = "0"
        when (Health) {
            "Saludable" ->  {
                HealthAux = "1"
            }
            "Grave" -> {
                HealthAux = "2"
            }
            "Critico" -> {
                HealthAux = "3"
            }
            "No especificado" -> {
                HealthAux = "0"
            }
        }


        val Quantity = "1"
        val Fee = "0"
        val State = "41326"
        val VideoAmt = "0"

        val Description = binding.descripcionEditText.text.toString()

       //Crear ID
        val uuid = UUID.randomUUID().toString().replace("-", "")
        val ShortUUID = uuid.substring(0, 9)

        // println("uuid = $ShortUUID")



        val PetID = ShortUUID

        saveSharedPreferencepetIDVISION(PetID)


        val RescuerID = "2ece3b2573dcdcebd774e635dca15fd9"
        val PhotoAmt = "1"
        val AdoptionSpeed = "0"

                 //nuevos
        val publicationDate_Mascota =""
        val ImageUrl= PetID + "-1.jpg"

      //  val telefono ="0966842158"
        val telefono = getSharedPreferenceUserNumber()

        //val refugio ="PAE"

        val refugio = getSharedPreferenceUserName()


        val publication = AdoptionPublication( Type.toString(),Name,Age,BreedAux,Breed2, GenderAux,Color1Aux,Color2Aux,Color3,MaturitySizeAux,FurLength,VaccinatedAux,DewormedAux,SterilizedAux,HealthAux,
            Description, Quantity, Fee,State,VideoAmt,PetID,RescuerID,PhotoAmt,AdoptionSpeed,publicationDate_Mascota,ImageUrl, telefono,refugio
                )

       // viewModel.publish(publication,PetID)

        Toast.makeText(requireContext(), "Datos correctos", Toast.LENGTH_SHORT).show()




        val intent = Intent (getActivity(), MainActivityVision::class.java)
        intent.putExtra("PUBLICACTION_SERIAL", publication);
        intent.putExtra("URIPET", viewModel.imageUri.value.toString())

        getActivity()?.startActivity(intent)


    }



    private fun loadWithPicasso(uri: Uri) {
        if (uri != Uri.EMPTY) {
            Picasso.get()
                .load(uri)
              //  .fit().into(binding.uploadImageView)
        } else  {
            Picasso.get()
                .load(R.drawable.place_holder)
            //    .fit().into(binding.uploadImageView)
        }

    }

    private fun clear(){
       // binding.uploadImageView.setImageResource(0)
      //  removeImage()
        binding.autoCompleteAnimalType.text.clear()
        binding.autoCompleteColorType .text.clear()
        binding.autoCompletebreedType .text.clear()
        binding.autoCompleteGeneroType  .text.clear()
        binding.autoCompleteDewormedType  .text.clear()
        binding.autoCompleteHealthType .text.clear()
        binding.autoCompleteSizeType .text.clear()
        binding.autoCompleteSterilizedType .text.clear()
        binding.autoCompleteVacinatedType  .text.clear()

        binding.edadEditText   .text.clear ()
        binding.nombreEditText.text.clear ()

        binding.descripcionEditText  .text.clear()




    }

    private fun removeImage(){
        Picasso.get()
            .load(R.drawable.place_holder)
        //    .fit().into(binding.uploadImageView)
    }




    fun getSharedPreferenceUserName(): String? {
        var editorSP = AppDatabase.getShareDB()
        var UserName = editorSP.getString("UserName", "null")

        return UserName

    }

    fun getSharedPreferenceUserNumber(): String? {
        var editorSP = AppDatabase.getShareDB()
        var UserNumber = editorSP.getString("UserNumber", "0956418789")

        return UserNumber

    }


    fun saveSharedPreferencepetIDVISION(petID: String) {
        var editorSP = AppDatabase.getShareDB().edit()
        editorSP.putString ("petIDVISION", petID )
        editorSP.commit()
    }


}






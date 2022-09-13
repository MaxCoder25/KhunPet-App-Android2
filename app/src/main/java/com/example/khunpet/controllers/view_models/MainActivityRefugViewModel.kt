package com.example.khunpet.controllers.view_models

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.R
import com.example.khunpet.model.Usuario
import com.example.khunpet.ui.fragments.FoundListFragmentRefug
import com.example.khunpet.ui.fragments.HomeFragmentRefug
import com.example.khunpet.ui.fragments.InsertFoundFragment
import com.example.khunpet.ui.fragments.InsertPublicationFragmentRefug
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivityRefugViewModel : ViewModel() {

    private val homeFragmentRefug = HomeFragmentRefug()
    private val insertPublicationFragmentRefug = InsertPublicationFragmentRefug()
    private val insertFoundFragment = InsertFoundFragment()
    private val foundFragmentRefug = FoundListFragmentRefug()


    val currentFragment : MutableLiveData<Fragment> by lazy {
        MutableLiveData<Fragment>()
    }
    val currentTab : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    init {
        currentFragment.postValue(homeFragmentRefug)
        currentTab.postValue(1)
    }


    fun changeFragment(index:Int) : Boolean {
        changeTab(index)
        return when(index) {
            R.id.home_button_refug -> {
                currentFragment.postValue(homeFragmentRefug)
                true
            }
            R.id.publications_button_refug -> {
                currentFragment.postValue(insertPublicationFragmentRefug)
                true
            }
            R.id.publications_found_insertar_button -> {
                currentFragment.postValue(insertFoundFragment)
                true
            }
            R.id.publications_found_button -> {
                currentFragment.postValue(foundFragmentRefug)
                true
            }
            else -> false
        }
    }

    fun changeTab(index:Int) {
        currentTab.postValue(index)
    }

}
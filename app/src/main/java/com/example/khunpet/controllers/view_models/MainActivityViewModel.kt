package com.example.khunpet.controllers.view_models

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.R
import com.example.khunpet.model.Usuario
import com.example.khunpet.ui.fragments.*
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivityViewModel: ViewModel() {

    private val lostFoundFragment = LostAndFoundFragment()
    private val homeFragment = HomeFragment()
    private val mapFragment = MapFragment()
    private val insertPublicationsFragment = InsertPublicationFragment()
    private val userFragment = UserFragment()
    private val foundFragment = FoundListFragment()

    val currentFragment : MutableLiveData<Fragment> by lazy {
        MutableLiveData<Fragment>()
    }
    val currentTab : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }



    init {
        currentFragment.postValue(homeFragment)
        currentTab.postValue(1)

    }

    fun changeFragment(index:Int) : Boolean {
        changeTab(index)
        return when(index) {
            R.id.home_button -> {
                currentFragment.postValue(homeFragment)
                true
            }
            R.id.lost_found_button -> {
                currentFragment.postValue(lostFoundFragment)
                true
            }
            R.id.publications_button -> {
                currentFragment.postValue(insertPublicationsFragment)
                true
            }
            R.id.usuario_button -> {
                currentFragment.postValue(userFragment)
                true
            }
            R.id.encontrados_button -> {
                currentFragment.postValue(foundFragment)
                true
            }
            10 -> {
                currentFragment.postValue(mapFragment)
                true
            }
            else -> false
        }
    }

    fun changeTab(index:Int) {
        currentTab.postValue(index)
    }

}
package com.example.khunpet.controllers.view_models

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.R
import com.example.khunpet.ui.fragments.HomeFragmentRefug
import com.example.khunpet.ui.fragments.InsertPublicationFragmentRefug


class MainActivityRefugViewModel : ViewModel() {

    private val homeFragmentRefug = HomeFragmentRefug()
    private val insertPublicationFragmentRefug = InsertPublicationFragmentRefug()

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

   /* fun changeFragment(index:Int) : Boolean {
        changeTab(index)
        return when(index) {
            3 -> {
                currentFragment.postValue(homeFragmentRefug)
                true
            }
            R.id.publications_button_refug -> {
                currentFragment.postValue(insertPublicationFragmentRefug)
                true
            }
            else -> false
        }
    }*/

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
            else -> false
        }
    }

    fun changeTab(index:Int) {
        currentTab.postValue(index)
    }

}
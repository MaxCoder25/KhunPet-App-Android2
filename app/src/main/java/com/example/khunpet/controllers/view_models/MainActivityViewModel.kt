package com.example.khunpet.controllers.view_models

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.R
import com.example.khunpet.ui.fragments.HomeFragment
import com.example.khunpet.ui.fragments.LostAndFoundFragment

class MainActivityViewModel: ViewModel() {

    private val lostFoundFragment = LostAndFoundFragment()
    private val homeFragment = HomeFragment()

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
            else -> false
        }
    }

    fun changeTab(index:Int) {
        currentTab.postValue(index)
    }


}
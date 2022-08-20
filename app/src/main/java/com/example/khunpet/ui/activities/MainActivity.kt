package com.example.khunpet.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.MainActivityViewModel
import com.example.khunpet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    val viewModel : MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.navBar.setOnItemSelectedListener {
            viewModel.changeFragment(it.itemId)
        }

        viewModel.currentFragment.observe(this, Observer {
            cambiarFragmento(it)
        })

        viewModel.currentTab.observe(this, Observer {
            binding.navBar.selectedItemId = it
        })


    }

    override fun onBackPressed() {
        if (viewModel.currentTab.value == R.id.home_button) {
            super.onBackPressed()
        } else {
            viewModel.changeFragment(R.id.home_button)
        }
    }

    private fun cambiarFragmento(fragmento : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .replace(binding.fragmentContainer.id, fragmento)
            .commit()
    }


}
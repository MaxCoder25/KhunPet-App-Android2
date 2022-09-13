package com.example.khunpet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.MainActivityRefugViewModel
import com.example.khunpet.databinding.ActivityMainRefugBinding
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.auth.FirebaseAuth

class MainActivityRefug: AppCompatActivity() {

    private lateinit var binding : ActivityMainRefugBinding
    private val viewModel : MainActivityRefugViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRefugBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.navBarRefug.setOnItemSelectedListener {
            viewModel.changeFragment(it.itemId)
        }

        viewModel.currentFragment.observe(this, Observer {
            cambiarFragmento(it)
        })

        viewModel.currentTab.observe(this, Observer {

            binding.navBarRefug.selectedItemId = it
        })


    }

    override fun onBackPressed() {
        if (viewModel.currentTab.value == R.id.home_button_refug) {
            super.onBackPressed()
        } else {
            viewModel.changeFragment(R.id.home_button_refug)
        }
    }

    private fun cambiarFragmento(fragmento : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .replace(binding.fragmentContainerRefug.id, fragmento)
            .commit()
    }

    //Daniel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.app_botton -> reload()
            R.id.sign_out -> signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reload() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        this.startActivity(intent)
        onBackPressed()
    }


}
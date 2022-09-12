package com.example.khunpet.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage


class AppDatabase : Application() {

    companion object {

        private lateinit var storage: FirebaseStorage
        private lateinit var dbShare: SharedPreferences
        private lateinit var auth: FirebaseAuth
        private lateinit var currentUser : FirebaseUser


        fun getStorageReference() : FirebaseStorage {
            return storage
        }


        fun getShareDB(): SharedPreferences {
            return dbShare
        }

        fun getAuthInstance(): FirebaseAuth {
            return auth
        }

        fun setCurrentUser(user : FirebaseUser) {
            currentUser = user
        }

        fun getCurrentUser() : FirebaseUser {
            return currentUser
        }

    }

    override fun onCreate() {
        super.onCreate()
        storage = FirebaseStorage.getInstance()
        dbShare = applicationContext.getSharedPreferences("confData", Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
    }

}
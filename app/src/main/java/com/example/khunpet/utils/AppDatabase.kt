package com.example.khunpet.utils

import android.app.Application
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class AppDatabase : Application() {

    companion object {

        private lateinit var storage: FirebaseStorage

        fun getStorageReference() : FirebaseStorage {
            return storage
        }

    }

    override fun onCreate() {
        super.onCreate()
        storage = FirebaseStorage.getInstance()

    }

}
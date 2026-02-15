package com.example.nutriragente.ui.login

import com.example.nutriragente.data.repository.UserRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class LogCasResViewModel {
    private val db = FirebaseFirestore.getInstance(FirebaseApp.getInstance())
    private val userRepository = UserRepository()

    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        userRepository.resetPassword(email, onResult)

    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        userRepository.login(email, password, onResult)
    }

    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        userRepository.register(email, password, onResult)
    }

    fun getCurrentUserId(): String? {
        return userRepository.getCurrentUserId()
    }




}
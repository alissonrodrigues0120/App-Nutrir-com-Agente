package com.example.nutriragente.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nutriragente.data.remote.UserRepository

class AuthViewModel : ViewModel() {

    private val repository = UserRepository()

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        repository.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        repository.register(email, password, callback)
    }

    fun resetPassword(email: String, callback: (Boolean, String?) -> Unit) {
        repository.resetPassword(email, callback)
    }

    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }

    fun logout() {
        repository.logout()
    }
}

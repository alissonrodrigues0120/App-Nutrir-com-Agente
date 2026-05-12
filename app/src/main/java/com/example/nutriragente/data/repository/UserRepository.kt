package com.example.nutriragente.data.repository

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun register(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun resetPassword(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun logout() {
        auth.signOut()
    }
}
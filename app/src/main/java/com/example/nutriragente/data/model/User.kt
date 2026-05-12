package com.example.nutriragente.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val senha: String = "",
    val name: String = ""
)
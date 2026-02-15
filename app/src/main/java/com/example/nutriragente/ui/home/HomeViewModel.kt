package com.example.nutriragente.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriragente.data.repository.CriancaRepository
import com.example.nutriragente.data.model.Crianca
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
class HomeViewModel(application: Application, private val userId: String) : AndroidViewModel(application) {




    private val repository: CriancaRepository


    init {
        val db = FirebaseFirestore.getInstance(FirebaseApp.getInstance())

        repository = CriancaRepository(userId)
    }

    var criancas: Flow<List<Crianca>> = repository.getCriancas()


    fun reloadData() {
        viewModelScope.launch {
            criancas = repository.getCriancas()
        }
    }


}

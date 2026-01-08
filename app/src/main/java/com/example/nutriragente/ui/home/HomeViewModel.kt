package com.example.nutriragente.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.nutriragente.data.database.entities.Crianca
import com.example.nutriragente.data.database.repository.CriancaRepository
import com.example.nutriragente.data.database.NutrirDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) { 

    private val repository: CriancaRepository
    val criancas: LiveData<List<Crianca>>

    init {
        val db = NutrirDatabase.getDatabase(application)
        val criancaDao = db.CriancaDao()

        repository = CriancaRepository(criancaDao)
        criancas = repository.getAllKids()     }

    fun addCrianca(c: Crianca) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertKids(c)
        }
    }
}

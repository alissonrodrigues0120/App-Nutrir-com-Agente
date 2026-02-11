package com.example.nutriragente.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutriragente.data.database.entities.Crianca
import com.example.nutriragente.data.database.repository.CriancaRepository
import com.example.nutriragente.data.database.NutrirDatabase
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CriancaRepository
    private val _criancas = MutableLiveData<List<Crianca>>(emptyList())
    val criancas: LiveData<List<Crianca>> = _criancas



    init {
        val db = NutrirDatabase.getDatabase(application)
        val criancaDao = db.CriancaDao()

        repository = CriancaRepository(criancaDao)
    }

    fun reloadData() {
        viewModelScope.launch {
            val data : LiveData<List<Crianca>> = repository.getAllKids()
            _criancas.value = data.value ?: emptyList()
        }
    }


}

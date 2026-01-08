package com.example.nutriragente.data.database.repository

import com.example.nutriragente.data.database.dao.CriancaDao
import androidx.lifecycle.LiveData
import com.example.nutriragente.data.database.entities.Crianca
class CriancaRepository(private val criancadao: CriancaDao){
    suspend fun insertKids(crianca : Crianca) = criancadao.insert(crianca)

    suspend fun updateKids(crianca : Crianca) = criancadao.update(crianca)

    suspend fun deleteKids(crianca : Crianca) = criancadao.delete(crianca)

    fun getAllKids(): LiveData<List<Crianca>> = criancadao.getAll()

}

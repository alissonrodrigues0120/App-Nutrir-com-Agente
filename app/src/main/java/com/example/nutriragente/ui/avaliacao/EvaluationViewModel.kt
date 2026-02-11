package com.example.nutriragente.ui.avaliacao

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.nutriragente.data.database.entities.Crianca
import com.example.nutriragente.data.database.dao.CriancaDao
import com.example.nutriragente.data.database.repository.CriancaRepository
import com.example.nutriragente.data.database.NutrirDatabase
import com.example.nutriragente.ui.home.GraphHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EvaluationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CriancaRepository
    
    init {
        val db = NutrirDatabase.getDatabase(application)
        repository = CriancaRepository(db.CriancaDao())
    }

    fun salvarAvaliacao(
        nome: String,
        peso: Double,
        alturaCm: Double,
        idadeMeses: Int,
        sexo: String,
        tipoAm: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val alturaM = alturaCm / 100.0

            // 1. Calcular IMC usando GraphHistory
            val imc = GraphHistory.calcularIMC(peso, alturaM)

            // 2. Calcular Z-Score
            val zScore = GraphHistory.calcularEscoreZ(imc, idadeMeses, sexo)

            // 3. Obter Classificação
            val status = GraphHistory.getClassificacao(zScore, idadeMeses)

            // Cria o objeto para o Banco de Dados
            val crianca = Crianca(
                nome = nome,
                idadeMeses = idadeMeses,
                peso = peso,
                altura = alturaM, // Salve em metros para manter o padrão
                sexo = sexo,
                tipoAm = tipoAm,
                imc = imc,
                imcZScore = zScore,
                statusNutricional = status
                // Se sua entidade Crianca tiver dataNascimento, adicione aqui
            )

            repository.insertKids(crianca)
            repository.updateKids(crianca)

            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), "Salvo: $status", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("EvaluationViewModel", "Erro: ${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun calculateAgeInMonths(birthTimestamp: Long): Int {
        val birthDate = java.util.Date(birthTimestamp)
        val today = java.util.Date()
        val diffMillis = today.time - birthDate.time
        return (diffMillis / (30 * 24 * 60 * 60 * 1000L)).toInt()
    }
}

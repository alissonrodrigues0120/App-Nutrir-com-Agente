package com.example.nutriragente.ui.avaliacao

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutriragente.R
import com.example.nutriragente.data.model.Crianca
import com.example.nutriragente.data.repository.CriancaRepository
import com.example.nutriragente.ui.home.GraphHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth

class EvaluationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CriancaRepository
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid
    
    private val _navegacaoEvent = MutableLiveData<Pair<Int, Bundle>?>()
    val navegacaoEvent: LiveData<Pair<Int, Bundle>?> = _navegacaoEvent

    init {
        repository = CriancaRepository(userId)
    }

    fun salvarAvaliacao(
        nome: String,
        peso: Double,
        alturaCm: Double,
        idadeMeses: Int,
        sexo: String,
        tipoAm: String,
        dataNascimento: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val alturaM = alturaCm / 100.0
            val imc = GraphHistory.calcularIMC(peso, alturaM)
            val zScore = GraphHistory.calcularEscoreZ(imc, idadeMeses, sexo)
            val status = GraphHistory.getClassificacao(zScore, idadeMeses)

            val crianca = Crianca(
                nome = nome,
                idadeMeses = idadeMeses,
                peso = peso,
                altura = alturaM,
                sexo = sexo,
                tipoAm = tipoAm,
                imc = imc,
                imcZScore = zScore,
                statusNutricional = status
            )

            val saveResult = repository.addCrianca(crianca)

            withContext(Dispatchers.Main) {
                saveResult.onSuccess { documentId ->
                    Toast.makeText(getApplication(), "Salvo: $status", Toast.LENGTH_LONG).show()
                    
                    val bundle = Bundle().apply {
                        putString("USER_ID", userId)
                        putString("CHILD_ID", documentId)
                        val partes = dataNascimento.split("/")
                        val isoDate = if(partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else dataNascimento
                        putString("BIRTH_DATE", isoDate)
                    }

                    // Usando as ACTIONS definidas no nav_graph.xml
                    val actionId = when {
                        idadeMeses < 6 -> R.id.action_new_evaluation_to_form_sixmonth
                        idadeMeses in 6..23 -> R.id.action_new_evaluation_to_form_sixtotwentythree
                        else -> R.id.action_new_evaluation_to_form_twoyears
                    }
                    
                    _navegacaoEvent.value = Pair(actionId, bundle)
                }.onFailure { exception ->
                    Toast.makeText(getApplication(), "Erro ao salvar: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("EvaluationViewModel", "Erro: ${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun resetNavegacao() {
        _navegacaoEvent.value = null
    }
}

package com.example.nutriragente.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriragente.data.model.Crianca
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Contadores derivados da lista em memória — sem queries extras ao servidor.
 * Funciona 100% offline.
 */
data class Contadores(
    val total: Int = 0,
    val pesoIdeal: Int = 0,
    val baixoPeso: Int = 0,
    val sobrepeso: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val userId get() = auth.currentUser?.uid

    /**
     * ÚNICA query Firestore — fonte de verdade da Home.
     *
     * O SDK do Firestore já mantém um listener de snapshot ativo internamente.
     * Quando o app fica offline, o cache local (PersistentCache configurado no
     * FirebaseModule) entrega os dados sem nenhuma alteração de código aqui.
     *
     * SharingStarted.WhileSubscribed(5_000): o listener é removido do Firestore
     * 5 s após o último coletor se desinscrever, evitando leak mesmo que o
     * ViewModel sobreviva à view por um breve momento.
     */
    val todasCriancas: StateFlow<List<Crianca>> = flow {
        val uid = userId ?: return@flow
        db.collection("users").document(uid).collection("crianca")
            .asFlow()
            .map { snapshot -> snapshot.toObjects(Crianca::class.java) }
            .collect { emit(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // ── Filtros derivados da mesma lista em memória ─────────────────────────
    // Nenhuma query adicional ao Firestore. Funciona offline automaticamente.

    val baixoPeso: StateFlow<List<Crianca>> = todasCriancas.map { lista ->
        lista.filter { it.statusNutricional in listOf("Magreza", "Magreza Acentuada") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val sobrepeso: StateFlow<List<Crianca>> = todasCriancas.map { lista ->
        lista.filter { it.statusNutricional in listOf("Sobrepeso", "Obesidade", "Obesidade Grave") }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val pesoIdeal: StateFlow<List<Crianca>> = todasCriancas.map { lista ->
        // GraphHistory.getClassificacao() grava "Peso Adequado" no Firestore.
        lista.filter { it.statusNutricional == "Peso Adequado" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Substitui as 4 chamadas AggregateSource.SERVER (que falhavam offline).
     * Os contadores são calculados na memória, a partir do StateFlow já ativo.
     *
     * Atenção: os valores abaixo devem espelhar exatamente o que
     * GraphHistory.getClassificacao() grava no campo statusNutricional.
     */
    val contadores: StateFlow<Contadores> = todasCriancas.map { lista ->
        Contadores(
            total      = lista.size,
            pesoIdeal  = lista.count { it.statusNutricional == "Peso Adequado" },
            baixoPeso  = lista.count { it.statusNutricional in listOf("Magreza", "Magreza Acentuada") },
            sobrepeso  = lista.count { it.statusNutricional in listOf("Sobrepeso", "Obesidade", "Obesidade Grave") }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Contadores())
}

/**
 * Converte uma Query Firestore em Flow coroutine-safe.
 * O listener é removido automaticamente via awaitClose quando o Flow é cancelado.
 */
private fun Query.asFlow(): Flow<QuerySnapshot> = callbackFlow {
    val listener = addSnapshotListener { snapshot, error ->
        if (error != null) { close(error); return@addSnapshotListener }
        snapshot?.let { trySend(it) }
    }
    awaitClose { listener.remove() }
}

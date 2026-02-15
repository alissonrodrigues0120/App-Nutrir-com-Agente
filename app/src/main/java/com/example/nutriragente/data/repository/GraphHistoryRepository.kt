package com.example.nutriragente.data.repository

import com.example.nutriragente.data.model.GraphHistory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GraphHistoryRepository (private val userId: String, private val criancaId: String) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("users").document(userId).collection("crianca").document(criancaId).collection("graphHistory")

    // 🔹 Adicionar
    suspend fun addGraphHistory(graph: GraphHistory): Boolean {
        return try {
            collection.add(graph.toMap()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 🔹 Buscar todos
    suspend fun getGraphHistory(): List<GraphHistory> {
        return try {
            val snapshot = collection
                .orderBy("date")
                .get()
                .await()

            snapshot.documents.map {
                GraphHistory.fromSnapshot(it)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    // 🔹 Deletar
    suspend fun deleteGraphHistory(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

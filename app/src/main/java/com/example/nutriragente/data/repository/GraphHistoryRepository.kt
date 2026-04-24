package com.example.nutriragente.data.repository

import com.example.nutriragente.data.model.GraphHistory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GraphHistoryRepository (private val userId: String, private val criancaId: String) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("users").document(userId).collection("crianca").document(criancaId).collection("graphHistory")

    // 🔹 Adicionar
    suspend fun addGraphHistory(graph: GraphHistory): Result<Unit> {
        return try {
            collection.add(graph.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "GraphHistoryRepository",
                operation = "addGraphHistory",
                throwable = e,
                context = mapOf("userId" to userId, "criancaId" to criancaId)
            )
            Result.failure(e)
        }
    }

    // 🔹 Buscar todos
    suspend fun getGraphHistory(): Result<List<GraphHistory>> {
        return try {
            val snapshot = collection
                .orderBy("date")
                .get()
                .await()

            Result.success(snapshot.documents.map {
                GraphHistory.fromSnapshot(it)
            })

        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "GraphHistoryRepository",
                operation = "getGraphHistory",
                throwable = e,
                context = mapOf("userId" to userId, "criancaId" to criancaId)
            )
            Result.failure(e)
        }
    }

    // 🔹 Deletar
    suspend fun deleteGraphHistory(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "GraphHistoryRepository",
                operation = "deleteGraphHistory",
                throwable = e,
                context = mapOf(
                    "userId" to userId,
                    "criancaId" to criancaId,
                    "graphHistoryId" to id
                )
            )
            Result.failure(e)
        }
    }
}

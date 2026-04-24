package com.example.nutriragente.data.repository

import com.example.nutriragente.data.model.GraphHistory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repositório de histórico antropométrico.
 *
 * Mudanças em relação à versão anterior:
 * - addGraphHistory usa ID determinístico baseado em criancaId + date.
 *   Isso elimina a criação de documentos duplicados caso o ACS salve a
 *   mesma avaliação duas vezes (ex: duplo toque, reconexão com retry).
 * - SetOptions.merge() garante upsert: atualiza se existir, cria se não.
 * - FirebaseFirestore chega via DI (instância já configurada com cache offline).
 */
class GraphHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun collection(userId: String, criancaId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("crianca")
            .document(criancaId)
            .collection("graphHistory")

    // ==================== CREATE / UPSERT ====================

    /**
     * Salva um registro de histórico com ID determinístico.
     *
     * ID = "{criancaId}_{graph.date}"
     * → Mesmo que o método seja chamado duas vezes para a mesma criança
     *   na mesma data, o resultado final é UM único documento (upsert).
     */
    suspend fun addGraphHistory(
        userId: String,
        criancaId: String,
        graph: GraphHistory
    ): Result<Unit> {
        return try {
            val deterministicId = "${criancaId}_${graph.date}"
            collection(userId, criancaId)
                .document(deterministicId)
                .set(graph.toMap(), SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "GraphHistoryRepository",
                operation  = "addGraphHistory",
                throwable  = e,
                context    = mapOf("userId" to userId, "criancaId" to criancaId)
            )
            Result.failure(e)
        }
    }

    // ==================== READ ====================

    suspend fun getGraphHistory(userId: String, criancaId: String): Result<List<GraphHistory>> {
        return try {
            val snapshot = collection(userId, criancaId)
                .orderBy("date")
                .get()
                .await()

            Result.success(snapshot.documents.map { GraphHistory.fromSnapshot(it) })
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "GraphHistoryRepository",
                operation  = "getGraphHistory",
                throwable  = e,
                context    = mapOf("userId" to userId, "criancaId" to criancaId)
            )
            Result.failure(e)
        }
    }

    // ==================== DELETE ====================

    suspend fun deleteGraphHistory(
        userId: String,
        criancaId: String,
        id: String
    ): Result<Unit> {
        return try {
            collection(userId, criancaId).document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "GraphHistoryRepository",
                operation  = "deleteGraphHistory",
                throwable  = e,
                context    = mapOf(
                    "userId"         to userId,
                    "criancaId"      to criancaId,
                    "graphHistoryId" to id
                )
            )
            Result.failure(e)
        }
    }
}

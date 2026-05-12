package com.example.nutriragente.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.nutriragente.data.model.Crianca
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repositório injetável via Hilt.
 *
 * Mudanças em relação à versão anterior:
 * - FirebaseFirestore e FirebaseAuth chegam via DI (não mais getInstance() avulsos).
 *   Isso garante que a instância já está configurada com o cache offline
 *   definido no FirebaseModule antes de qualquer chamada.
 * - userId é resolvido dinamicamente a partir do auth em cada operação,
 *   eliminando o risco de usar um userId "velho" capturado no construtor.
 */
class CriancaRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val userId: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("Usuário não autenticado")

    private val criancaCollection
        get() = db.collection("users").document(userId).collection("crianca")

    // ==================== CREATE ====================

    suspend fun addCrianca(crianca: Crianca): Result<String> {
        return try {
            val documentRef = criancaCollection.document()
            documentRef.set(crianca.toMap()).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "CriancaRepository",
                operation  = "addCrianca",
                throwable  = e,
                context    = mapOf("userId" to userId)
            )
            Result.failure(e)
        }
    }

    // ==================== UPDATE ====================

    suspend fun updateCrianca(crianca: Crianca): Result<Unit> {
        return try {
            criancaCollection.document(crianca.id.toString())
                .update(crianca.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "CriancaRepository",
                operation  = "updateCrianca",
                throwable  = e,
                context    = mapOf("userId" to userId, "criancaId" to crianca.id)
            )
            Result.failure(e)
        }
    }

    // ==================== DELETE ====================

    suspend fun deletePatient(patientId: String): Result<Unit> {
        return try {
            criancaCollection.document(patientId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "CriancaRepository",
                operation  = "deletePatient",
                throwable  = e,
                context    = mapOf("userId" to userId, "patientId" to patientId)
            )
            Result.failure(e)
        }
    }
}

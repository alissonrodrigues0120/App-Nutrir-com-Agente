package com.example.nutriragente.data.repository

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.nutriragente.data.model.Crianca
import kotlinx.coroutines.tasks.await

class CriancaRepository(private val userId: String?) {

    private val user = userId ?: ""

    private val db = FirebaseFirestore.getInstance(FirebaseApp.getInstance())

    private val CriancaCollection = db.collection("users").document(user).collection("crianca")


    // Obter pacientes do usuário atual




    // Adicionar novo paciente
    suspend fun addCrianca(crianca: Crianca): Result<String> {
        return try {
            if (userId.isNullOrBlank()) {
                return Result.failure(IllegalStateException("Usuário não autenticado"))
            }
            val newCrianca = crianca
            val documentRef = CriancaCollection.document()
            documentRef.set(newCrianca.toMap()).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "CriancaRepository",
                operation = "addCrianca",
                throwable = e,
                context = mapOf("userId" to (userId ?: "null"))
            )
            Result.failure(e)
        }
    }

    // Atualizar paciente
    suspend fun updateCrianca(crianca: Crianca): Result<Unit> {
        return try {
            val documentRef = CriancaCollection.document(crianca.id.toString())
            documentRef.update(crianca.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "CriancaRepository",
                operation = "updateCrianca",
                throwable = e,
                context = mapOf(
                    "userId" to (userId ?: "null"),
                    "criancaId" to crianca.id
                )
            )
            Result.failure(e)
        }
    }

    // Excluir paciente
    suspend fun deletePatient(patientId: String): Result<Unit> {
        return try {
            CriancaCollection.document(patientId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "CriancaRepository",
                operation = "deletePatient",
                throwable = e,
                context = mapOf(
                    "userId" to (userId ?: "null"),
                    "patientId" to patientId
                )
            )
            Result.failure(e)
        }
    }


}
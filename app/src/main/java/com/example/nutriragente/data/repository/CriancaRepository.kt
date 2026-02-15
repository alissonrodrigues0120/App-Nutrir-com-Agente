package com.example.nutriragente.data.repository

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.nutriragente.data.model.Crianca
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CriancaRepository(private val userId: String?) {

    private val user = userId as String

    private val db = FirebaseFirestore.getInstance(FirebaseApp.getInstance())

    private val CriancaCollection = db.collection("users").document(user).collection("crianca")


    // Obter pacientes do usuário atual
    fun getCriancas(): Flow<List<Crianca>> = callbackFlow {
        val query = CriancaCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            snapshot?.let {
                trySend(
                    it.documents.map { doc ->
                        Crianca.fromSnapshot(doc)
                    }
                )
            }
        }

        awaitClose { listener.remove() }
    }



    // Adicionar novo paciente
    suspend fun addCrianca(crianca: Crianca): String? {
        return try {
            val newCrianca = crianca
            val documentRef = CriancaCollection.document()
            documentRef.set(newCrianca.toMap()).await()
            documentRef.id
        } catch (e: Exception) {
            null
        }
    }

    // Atualizar paciente
    suspend fun updateCrianca(crianca: Crianca): Boolean {
        return try {
            val documentRef = CriancaCollection.document(crianca.id.toString())
            documentRef.update(crianca.toMap()).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Excluir paciente
    suspend fun deletePatient(patientId: String): Boolean {
        return try {
            CriancaCollection.document(patientId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }


}
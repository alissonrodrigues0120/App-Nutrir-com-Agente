package com.example.nutriragente.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.nutriragente.data.model.Forms
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await


class FormRepository (private val userId: String?, private val childId: String?, private val formType: String? ) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val formCollection = db.collection("users").document(userId.toString()).collection("crianca").document(childId.toString()).collection("forms")

    // ==================== CREATE / UPDATE ====================

    suspend fun saveForm(formResponse: Forms): Result<String> {
        return try {
            // Validar autenticação
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != userId) {
                return Result.failure(Exception("Usuário não autorizado"))
            }

            // Verificar se já existe um form deste tipo
            val existingForm = getFormByType(formType ?: "").firstOrNull()

            val formWithMetadata = formResponse.copy(
                updatedAt = System.currentTimeMillis(),
                userId = userId ?: "",
                childId = childId ?: "",
                formType = formType ?: ""
            )

            val formId = if (existingForm != null) {
                // Atualizar existente
                formCollection.document(existingForm.id)
                    .update(formWithMetadata.toMap())
                    .await()
                existingForm.id
            } else {
                // Criar novo
                val docRef = formCollection.add(formWithMetadata.toMap()).await()
                docRef.id
            }

            Result.success(formId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== READ ====================

    // Obter todos os forms da criança
    fun getAllForms(): Flow<List<Forms>> {
        return callbackFlow {
            val listener = formCollection
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val forms = snapshot?.documents?.mapNotNull { doc ->
                        Forms.fromMap(doc.id, doc.data ?: emptyMap())
                    } ?: emptyList()

                    trySend(forms)
                }

            awaitClose { listener.remove() }
        }
    }

    // Obter form específico por tipo
    fun getFormByType(formType: String): Flow<Forms?> {
        return callbackFlow {
            val listener = formCollection
                .whereEqualTo("formType", formType)
                .limit(1)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(null)
                        return@addSnapshotListener
                    }

                    val doc = snapshot?.documents?.firstOrNull()
                    val form = doc?.let {
                        Forms.fromMap(it.id, it.data ?: emptyMap())
                    }

                    trySend(form)
                }

            awaitClose { listener.remove() }
        }
    }

    // Obter form específico por ID
    fun getFormById(formId: String): Flow<Forms?> {
        return callbackFlow {
            val listener = formCollection.document(formId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(null)
                        return@addSnapshotListener
                    }

                    val form = snapshot?.let {
                        Forms.fromMap(it.id, it.data ?: emptyMap())
                    }

                    trySend(form)
                }

            awaitClose { listener.remove() }
        }
    }

    // Obter último formulário atualizado
    suspend fun getLatestForm(): Forms? {
        return try {
            val snapshot = formCollection
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                Forms.fromMap(doc.id, doc.data ?: emptyMap())
            }
        } catch (e: Exception) {
            null
        }
    }

    // ==================== DELETE ====================

    suspend fun deleteForm(formId: String): Result<Unit> {
        return try {
            formCollection.document(formId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFormByType(formType: String): Result<Unit> {
        return try {
            val snapshot = formCollection
                .whereEqualTo("formType", formType)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.reference?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== UTILS ====================

    // Verificar se form existe
    suspend fun formExists(formType: String): Boolean {
        return try {
            val snapshot = formCollection
                .whereEqualTo("formType", formType)
                .limit(1)
                .get()
                .await()
            snapshot.documents.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    // Obter data da última submissão
    suspend fun getLastSubmissionDate(formType: String): Long? {
        return try {
            val snapshot = formCollection
                .whereEqualTo("formType", formType)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.getLong("updatedAt")
        } catch (e: Exception) {
            null
        }
    }



}
package com.example.nutriragente.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.example.nutriragente.data.model.Forms
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FormRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private var userId: String? = null
    private var childId: String? = null
    private var formType: String? = null

    constructor(userId: String?, childId: String?, formType: String? = null) : this(
        FirebaseFirestore.getInstance(),
        FirebaseAuth.getInstance()
    ) {
        this.userId = userId
        this.childId = childId
        this.formType = formType
    }

    private val activeUserId: String?
        get() = userId ?: auth.currentUser?.uid

    private val formCollection
        get() = db.collection("users")
            .document(activeUserId ?: "")
            .collection("crianca")
            .document(childId ?: "")
            .collection("forms")

    // ==================== CREATE / UPDATE ====================

    suspend fun saveForm(formResponse: Forms): Result<String> {
        val repository = "FormRepository"
        val operation = "saveForm"
        return try {
            val safeUserId = activeUserId ?: return Result.failure(IllegalStateException("userId não informado"))
            val safeChildId = childId ?: formResponse.childId.ifEmpty { null } ?: return Result.failure(IllegalStateException("childId não informado"))
            val resolvedFormType = formType ?: formResponse.formType.ifEmpty { null } ?: return Result.failure(IllegalStateException("formType não informado"))

            val currentAuthUid = auth.currentUser?.uid
            if ((currentAuthUid != null) && (safeUserId != currentAuthUid)) {
                return Result.failure(Exception("Usuário não autorizado"))
            }

            val deterministicId = buildDeterministicId(safeUserId, safeChildId, resolvedFormType)

            val formWithMetadata = formResponse.copy(
                updatedAt = System.currentTimeMillis(),
                userId = safeUserId,
                childId = safeChildId,
                formType = resolvedFormType
            )

            db.collection("users")
                .document(safeUserId)
                .collection("crianca")
                .document(safeChildId)
                .collection("forms")
                .document(deterministicId)
                .set(formWithMetadata.toMap(), SetOptions.merge())
                .await()

            Result.success(deterministicId)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = repository,
                operation = operation,
                throwable = e,
                context = mapOf(
                    "userId" to (userId ?: "null"),
                    "childId" to (childId ?: "null"),
                    "formType" to (formType ?: "null")
                )
            )
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
                        RepositoryDiagnostics.logError(
                            repository = "FormRepository",
                            operation = "getAllForms",
                            throwable = error,
                            context = mapOf(
                                "userId" to (userId ?: "null"),
                                "childId" to (childId ?: "null")
                            )
                        )
                        close(error)
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
            val deterministicId = buildDeterministicId(
                userId = activeUserId ?: "missing-user",
                childId = childId ?: "missing-child",
                formType = formType
            )
            val listener = formCollection.document(deterministicId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        RepositoryDiagnostics.logError(
                            repository = "FormRepository",
                            operation = "getFormByType",
                            throwable = error,
                            context = mapOf(
                                "userId" to (userId ?: "null"),
                                "childId" to (childId ?: "null"),
                                "formType" to formType
                            )
                        )
                        close(error)
                        return@addSnapshotListener
                    }

                    val form = snapshot?.takeIf { it.exists() }?.let {
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
                        RepositoryDiagnostics.logError(
                            repository = "FormRepository",
                            operation = "getFormById",
                            throwable = error,
                            context = mapOf(
                                "userId" to (userId ?: "null"),
                                "childId" to (childId ?: "null"),
                                "formId" to formId
                            )
                        )
                        close(error)
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
            RepositoryDiagnostics.logError(
                repository = "FormRepository",
                operation = "getLatestForm",
                throwable = e,
                context = mapOf(
                    "userId" to (userId ?: "null"),
                    "childId" to (childId ?: "null")
                )
            )
            null
        }
    }

    // ==================== DELETE ====================

    suspend fun deleteForm(formIdOrChildId: String): Result<Unit> {
        return try {
            val targetChildId = childId ?: formIdOrChildId
            val collection = db.collection("users")
                .document(activeUserId ?: "")
                .collection("crianca")
                .document(targetChildId)
                .collection("forms")

            if (childId != null) {
                collection.document(formIdOrChildId).delete().await()
            } else {
                val snapshot = collection.get().await()
                for (doc in snapshot.documents) {
                    doc.reference.delete().await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "FormRepository",
                operation = "deleteForm",
                throwable = e,
                context = mapOf(
                    "userId" to (activeUserId ?: "null"),
                    "childId" to (childId ?: formIdOrChildId),
                    "formId" to formIdOrChildId
                )
            )
            Result.failure(e)
        }
    }

    suspend fun deleteFormByType(formType: String): Result<Unit> {
        return try {
            val safeUserId = activeUserId ?: return Result.failure(IllegalStateException("userId não informado"))
            val safeChildId = childId ?: return Result.failure(IllegalStateException("childId não informado"))
            val deterministicId = buildDeterministicId(safeUserId, safeChildId, formType)
            db.collection("users")
                .document(safeUserId)
                .collection("crianca")
                .document(safeChildId)
                .collection("forms")
                .document(deterministicId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "FormRepository",
                operation = "deleteFormByType",
                throwable = e,
                context = mapOf(
                    "userId" to (userId ?: "null"),
                    "childId" to (childId ?: "null"),
                    "formType" to formType
                )
            )
            Result.failure(e)
        }
    }

    // ==================== UTILS ====================

    // Verificar se form existe
    suspend fun formExists(formType: String): Boolean {
        return try {
            val safeUserId = activeUserId ?: return false
            val safeChildId = childId ?: return false
            val deterministicId = buildDeterministicId(safeUserId, safeChildId, formType)
            db.collection("users")
                .document(safeUserId)
                .collection("crianca")
                .document(safeChildId)
                .collection("forms")
                .document(deterministicId)
                .get()
                .await()
                .exists()
        } catch (e: Exception) {
            RepositoryDiagnostics.logError(
                repository = "FormRepository",
                operation = "formExists",
                throwable = e,
                context = mapOf(
                    "userId" to (userId ?: "null"),
                    "childId" to (childId ?: "null"),
                    "formType" to formType
                )
            )
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
            RepositoryDiagnostics.logError(
                repository = "FormRepository",
                operation = "getLastSubmissionDate",
                throwable = e,
                context = mapOf(
                    "userId" to (userId ?: "null"),
                    "childId" to (childId ?: "null"),
                    "formType" to formType
                )
            )
            null
        }
    }

    private fun buildDeterministicId(userId: String, childId: String, formType: String): String {
        return "${userId}_${childId}_$formType"
    }
}

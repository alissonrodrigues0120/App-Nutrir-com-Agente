package com.example.nutriragente.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot

data class GraphHistory(
    @DocumentId
    val id: String = "",   // ⚠️ Firestore usa String como ID do documento
    val title: String,
    val description: String,
    val score: Float,
    val date: Long = System.currentTimeMillis()
) {

    // Converter para Map para o Firestore
    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "description" to description,
            "score" to score,
            "date" to date
        )
    }

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): GraphHistory {
            return GraphHistory(
                id = snapshot.id, // ID real do documento
                title = snapshot.getString("title") ?: "",
                description = snapshot.getString("description") ?: "",
                score = snapshot.getDouble("score")?.toFloat() ?: 0f,
                date = snapshot.getLong("date") ?: 0L
            )
        }
    }
}

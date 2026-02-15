package com.example.nutriragente.data.model

import com.google.firebase.firestore.DocumentId

data class Crianca (
    @DocumentId
    val id: Int = 0,
    val nome: String,
    val idadeMeses: Int,
    val peso: Double,
    val altura: Double,
    val sexo: String,
    val tipoAm: String,
    // Agora são colunas reais no banco
    val imc: Double,
    val imcZScore: Double,
    val statusNutricional: String

){
    // Converter para Map para o Firestore
    fun toMap(): Map<String, Any> {
        return mapOf(
            "nome" to nome,
            "idadeMeses" to idadeMeses,
            "peso" to peso,
            "altura" to altura,
            "sexo" to sexo,
            "tipoAm" to tipoAm,
            "imc" to imc,
            "imcZScore" to imcZScore,
            "statusNutricional" to statusNutricional
        )
    }

    // Converter de DocumentSnapshot
    companion object {
        fun fromSnapshot(snapshot: com.google.firebase.firestore.DocumentSnapshot): Crianca {
            return Crianca(
                id = snapshot.getLong("id")?.toInt() ?: 0,
                nome = snapshot.getString("nome") ?: "",
                idadeMeses = snapshot.getLong("idadeMeses")?.toInt() ?: 0,
                peso = snapshot.getDouble("peso") ?: 0.0,
                altura = snapshot.getDouble("altura") ?: 0.0,
                sexo = snapshot.getString("sexo") ?: "",
                tipoAm = snapshot.getString("tipoAm") ?: "",
                imc = snapshot.getDouble("imc") ?: 0.0,
                imcZScore = snapshot.getDouble("imcZScore") ?: 0.0,
                statusNutricional = snapshot.getString("statusNutricional") ?: ""
            )
        }
    }
}



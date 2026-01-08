package com.example.nutriragente.data.database.entities
import com.example.nutriragente.ui.home.GraphHistory
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crianca")
data class Crianca(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
)

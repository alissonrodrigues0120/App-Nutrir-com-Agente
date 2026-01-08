package com.example.nutriragente.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "graph_history")
data class GraphHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val score: Float,
    val date: Long = System.currentTimeMillis()
)

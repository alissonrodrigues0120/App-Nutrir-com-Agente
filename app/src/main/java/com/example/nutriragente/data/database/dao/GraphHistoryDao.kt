package com.example.nutriragente.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nutriragente.data.database.entities.GraphHistory

@Dao
interface GraphHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(graphHistory: GraphHistory)

    @Query("SELECT * FROM graph_history ORDER BY date DESC")
    fun getAll(): LiveData<List<GraphHistory>>
}
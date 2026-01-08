package com.example.nutriragente.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nutriragente.data.database.entities.Crianca

@Dao

interface CriancaDao {
        @Insert
        suspend fun insert(crianca: Crianca) // Insert a new task.
        @Update
        suspend fun update(crianca: Crianca) // Update an existing task.
        @Delete
        suspend fun delete(crianca: Crianca) // Delete a task.
        @Query("SELECT * FROM crianca ORDER BY id DESC")
        fun getAll(): LiveData<List<Crianca>> // Fetch all tasks.
    }


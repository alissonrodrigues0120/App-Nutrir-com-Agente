package com.example.nutriragente.data.database

import android.app.Application
import com.example.nutriragente.data.database.dao.CriancaDao
import  com.example.nutriragente.data.database.entities.Crianca
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase




@Database(entities = [Crianca::class], version = 1)
 abstract class NutrirDatabase : RoomDatabase() {
   abstract fun CriancaDao(): CriancaDao
    companion object {
        @Volatile
        private var INSTANCE: NutrirDatabase? = null
        fun getDatabase(context: Context): NutrirDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutrirDatabase::class.java,
                    "nutrir_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }


    }
}


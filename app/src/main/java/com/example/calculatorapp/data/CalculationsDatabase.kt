package com.example.calculatorapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calculatorapp.data.model.Calculation

@Database(entities = [Calculation::class], version = 1, exportSchema = false)
abstract class CalculationsDatabase: RoomDatabase() {

    abstract fun calculationsDao(): CalculationsDao

    companion object {
        @Volatile
        private var INSTANCE: CalculationsDatabase? = null

        fun getDatabase(context: Context): CalculationsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalculationsDatabase::class.java,
                "calculations_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
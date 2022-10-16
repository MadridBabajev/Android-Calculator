package com.example.calculatorapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.calculatorapp.data.model.Calculation

@Dao
interface CalculationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCalculation(calculation: Calculation)

    @Query("DELETE FROM calculations")
    suspend fun deleteAllCalculations()

    @Query("SELECT * FROM calculations ORDER BY id ASC")
    fun readAllData(): LiveData<List<Calculation>>
}
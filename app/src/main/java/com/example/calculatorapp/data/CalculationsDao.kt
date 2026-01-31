package com.example.calculatorapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.calculatorapp.data.model.Calculation

@Dao
interface CalculationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCalculation(calculation: Calculation)

    @Query("DELETE FROM calculations")
    fun deleteAllCalculations()

    @Query("SELECT * FROM calculations ORDER BY id ASC")
    fun readAllData(): LiveData<List<Calculation>>
}
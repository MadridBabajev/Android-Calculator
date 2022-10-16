package com.example.calculatorapp.data.repository

import androidx.lifecycle.LiveData
import com.example.calculatorapp.data.model.Calculation
import com.example.calculatorapp.data.CalculationsDao

class CalculationsRepository(private val calculationsDao: CalculationsDao) {
    val readAllData: LiveData<List<Calculation>> = calculationsDao.readAllData()

    suspend fun addCalculation(calculation: Calculation) {
        calculationsDao.addCalculation(calculation)
    }

    suspend fun deleteAllCalculations() {
        calculationsDao.deleteAllCalculations()
    }
}
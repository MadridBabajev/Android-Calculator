package com.example.calculatorapp.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.calculatorapp.data.CalculationsDatabase
import com.example.calculatorapp.data.model.Calculation
import com.example.calculatorapp.data.repository.CalculationsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalculationViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Calculation>>
    private val repository: CalculationsRepository

    init {
        val calculationsDao = CalculationsDatabase.getDatabase(application).calculationsDao()
        repository = CalculationsRepository(calculationsDao)
        readAllData = repository.readAllData
    }

    fun addCalculation(calculation: Calculation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCalculation(calculation)
        }
    }

    fun deleteAllCalculations() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllCalculations()
        }
    }
}
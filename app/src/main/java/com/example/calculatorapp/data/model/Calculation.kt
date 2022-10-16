package com.example.calculatorapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class Calculation (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val expression: String,
    val result: String
    )
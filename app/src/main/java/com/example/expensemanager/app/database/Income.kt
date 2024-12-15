package com.example.expensemanager.app.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_table")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Auto-generated unique ID
    val amount: Double,                               // Income amount
    val date: String,                                 // Date of income
    val source: String,                               // Source of income (e.g., salary, bonus)
    val description: String?                          // Optional description
)

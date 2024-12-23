package com.example.expensemanager.app.database

data class Transaction(
    val id: Int,
    val amount: Double,
    val date: String,
    val time: String,
    val type: String, // "Income" or "Expense"
    val sourceOrCategory: String, // Income source or Expense category name
    val categoryIcon: Int = 957, // Icon for the expense category (nullable for income)
    val categoryColor: String? = null // Color for the expense category in HEX format (nullable for income)
)
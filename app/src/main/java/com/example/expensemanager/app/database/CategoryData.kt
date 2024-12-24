package com.example.expensemanager.app.database

data class CategoryData(
    val id: Int,
    val amount: Double,
    val category_id: Int,
    val category_name: String,
    val categoryIcon: Int = 957,
    val categoryColor: String? = null
)
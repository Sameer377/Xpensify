package com.example.expensemanager.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE category_id = :categoryId")
    suspend fun getExpensesByCategory(categoryId: Int): List<Expense>
}

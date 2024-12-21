package com.example.expensemanager.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

//    @Query("SELECT * FROM expenses WHERE category_id = :categoryId")
//    suspend fun getExpensesByCategory(categoryId: Int): List<Expense>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: List<Expense>)

    @Query("SELECT SUM(amount) FROM expenses")
    suspend fun getTotalExpenses(): Double?
}

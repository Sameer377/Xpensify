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


    @Query("SELECT * FROM expenses WHERE date = :date")
    suspend fun getDayTransactions(date: String): List<Expense>

    @Query("""
        SELECT * FROM expenses
        WHERE date BETWEEN DATE(:startDate) AND DATE(:endDate)
    """)
    suspend fun getWeeklyTransactions(startDate: String, endDate: String): List<Expense>

    @Query("""
        SELECT * FROM expenses
        WHERE strftime('%Y', date) = :year
    """)
    suspend fun getYearTransactions(year: String): List<Expense>

    @Query("""SELECT SUM(amount) FROM expenses
            WHERE date BETWEEN :startDate AND :endDate""")
                suspend fun getTotalExpenseInRange(startDate: String, endDate: String): Double?

        @Query("""SELECT SUM(amount) FROM expenses
                WHERE date = :date""")
                suspend fun getTotalExpenseOfDate(date: String): Double?


    // Function to get total expense for a specific category for all records
    @Query("SELECT SUM(amount) FROM expenses WHERE category_id = :category")
    fun getTotalExpenseForCategory(category: Int): Double?

    // Function to get total expense for a specific category between two dates
    @Query("SELECT SUM(amount) FROM expenses WHERE category_id = :category AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpenseForCategoryInDateRange(category: Int, startDate: String, endDate: String): Double?
}

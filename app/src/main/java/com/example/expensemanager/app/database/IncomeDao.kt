package com.example.expensemanager.app.database

import androidx.room.*

@Dao
interface IncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income)  // Insert a new income

//    @Query("SELECT * FROM income_table ORDER BY date DESC")
//    suspend fun getAllIncomes(): List<Income>  // Retrieve all income records

    @Delete
    suspend fun deleteIncome(income: Income)   // Delete a specific income record


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(income: List<Income>)

    @Query("DELETE FROM income_table")
    suspend fun deleteAllIncomes()             // Delete all income records

    @Query("SELECT SUM(amount) FROM income_table")
    suspend fun getTotalIncome(): Double?

    @Query("""  SELECT SUM(amount) FROM income_table
            WHERE date BETWEEN :startDate AND :endDate""")
                suspend fun getTotalIncomeInRange(startDate: String, endDate: String): Double?

        @Query("""SELECT SUM(amount) FROM income_table
                WHERE date = :date""")
                suspend fun getTotalIncomeOfDate(date: String): Double?
}

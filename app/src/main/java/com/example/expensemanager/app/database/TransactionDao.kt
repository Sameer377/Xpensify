package com.example.expensemanager.app.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TransactionDao {

    @Query("""
        SELECT 
            id, 
            amount, 
            date, 
            time, 
            'Income' AS type, 
            source AS sourceOrCategory, 
            NULL AS categoryIcon, 
            NULL AS categoryColor 
        FROM income_table
        UNION ALL
        SELECT 
            expenses.id, 
            expenses.amount, 
            expenses.date, 
            expenses.time, 
            'Expense' AS type, 
            categories.name AS sourceOrCategory, 
            categories.icon AS categoryIcon, 
            categories.color AS categoryColor 
        FROM expenses
        INNER JOIN categories ON expenses.category_id = categories.id
        ORDER BY date DESC, time DESC
    """)
    suspend fun getAllTransactions(): List<Transaction>
}

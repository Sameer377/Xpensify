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
    WHERE date = :selectedDate
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
    WHERE expenses.date = :selectedDate
    ORDER BY time DESC
""")
    suspend fun getDayTransaction(selectedDate: String): List<Transaction>


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
    WHERE date BETWEEN :startDate AND :endDate
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
    WHERE expenses.date BETWEEN :startDate AND :endDate
    ORDER BY date DESC, time DESC
""")
    suspend fun getTransactionsInRange(startDate: String, endDate: String): List<Transaction>
}

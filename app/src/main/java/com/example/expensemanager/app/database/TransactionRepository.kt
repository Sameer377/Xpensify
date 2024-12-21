package com.example.expensemanager.app.database

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }
}
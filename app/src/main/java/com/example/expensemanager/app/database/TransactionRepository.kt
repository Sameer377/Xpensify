package com.example.expensemanager.app.database

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }

    suspend fun getDayTransactions(date:String): List<Transaction> {
        return transactionDao.getDayTransaction(date)
    }

    suspend fun getTransactionsInRange(startDate:String,endDate:String): List<Transaction> {
        return transactionDao.getTransactionsInRange(startDate,endDate)
    }
}
package com.example.expensemanager.app.database

class CategoriesRepository(private val appDatabase: AppDatabase) {

    suspend fun getCategories(): List<CategoryData> {
        return appDatabase.transactionDao().getCategoryData()
    }

    suspend fun getTotalExpense(): Double {
        return appDatabase.transactionDao().getTotalExpense()?.toDouble() ?: 0.0
    }

    suspend fun getTotalIncome(): Double {
        return appDatabase.transactionDao().getTotalIncome()?.toDouble() ?: 0.0
    }



    suspend fun getTotalExpenseInRange(
        startDate: String,
        endDate: String
    ): Double {
        return appDatabase.transactionDao()
            .getTotalExpenseInRange(startDate, endDate)?.toDouble() ?: 0.0
    }

    suspend fun getTotalIncomeInRange(
        startDate: String,
        endDate: String
    ): Double {
        return appDatabase.transactionDao()
            .getTotalIncomeInRange(startDate, endDate)?.toDouble() ?: 0.0
    }

    suspend fun getCategoriesinRange( startDate: String,
                                      endDate: String): List<CategoryData> {
        return appDatabase.transactionDao().getCategoryDataInRange(startDate, endDate)
    }

}

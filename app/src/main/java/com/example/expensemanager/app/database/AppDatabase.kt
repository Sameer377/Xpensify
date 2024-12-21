package com.example.expensemanager.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Category::class, Income::class,Expense::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_manager_db"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback for prepopulating default categories and income
    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                // Insert default categories
                getDatabase(context).categoryDao().insertCategories(defaultCategories)

//                // Insert default income
//                getDatabase(context).incomeDao().insertIncome(defaultIncome)
//                getDatabase(context).expenseDao().insertExpense(defaultExpense)

            }
        }
    }
}

val defaultCategories = listOf(
    Category(name = "Others", icon = 0, color = "#FF5733"),
    Category(name = "Home", icon = 0, color = "#FF5733"),
    Category(name = "Entertainment", icon = 0, color = "#2196F3"),
    Category(name = "Traveling", icon = 0, color = "#4CAF50"),
    Category(name = "Clothes", icon = 0, color = "#FFC107"),
    Category(name = "Sports", icon =0, color = "#9C27B0")
)

//val defaultIncome = listOf(
//    Income(amount = 0.0, date = "2024-12-12" ,time="12:30:30", source = "Salary", description = "Received salary from boss")
//)
//val defaultExpense = listOf(
//    Expense(amount = 0.0, date = "2024-12-12" ,time="12:30:30", category_id = 1)
//)

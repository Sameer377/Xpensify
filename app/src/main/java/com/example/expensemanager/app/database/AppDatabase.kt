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
           /*     getDatabase(context).incomeDao().insertIncome(defaultIncome)
                getDatabase(context).expenseDao().insertExpense(defaultExpense)
*/
            }
        }
    }
}

val defaultCategories = listOf(
    Category(name = "Others", icon = 957, color = "#000000"),
    Category(name = "Home", icon = 471, color = "#FF5733"),
    Category(name = "Entertainment", icon = 325, color = "#2196F3"),
    Category(name = "Traveling", icon = 391, color = "#4CAF50"),
    Category(name = "Books", icon = 116, color = "#FFC107"),
    Category(name = "Sports", icon =520, color = "#9C27B0")
)
val defaultExpense = listOf(
    Expense(amount = 100.0, date = "2024-11-12", time = "12:30:30", category_id = 1),
    Expense(amount = 200.0, date = "2024-11-15", time = "10:00:00", category_id = 2),
    Expense(amount = 300.0, date = "2024-11-17", time = "11:30:00", category_id = 3),
    Expense(amount = 150.0, date = "2024-11-18", time = "12:45:00", category_id = 4),
    Expense(amount = 50.0, date = "2024-11-20", time = "14:00:00", category_id = 5),
    Expense(amount = 75.0, date = "2024-11-12", time = "08:00:00", category_id = 6),
    Expense(amount = 125.0, date = "2024-11-13", time = "09:15:00", category_id = 1),
    Expense(amount = 175.0, date = "2024-11-14", time = "10:30:00", category_id = 2),
    Expense(amount = 225.0, date = "2024-11-15", time = "11:45:00", category_id = 3),
    Expense(amount = 275.0, date = "2024-11-16", time = "13:00:00", category_id = 4),
    Expense(amount = 50.0, date = "2024-12-12", time = "14:15:00", category_id = 5),
    Expense(amount = 90.0, date = "2024-12-13", time = "15:30:00", category_id = 6),
    Expense(amount = 120.0, date = "2024-12-14", time = "16:45:00", category_id = 1),
    Expense(amount = 180.0, date = "2024-12-15", time = "17:00:00", category_id = 2),
    Expense(amount = 240.0, date = "2024-12-16", time = "18:15:00", category_id = 3),
    Expense(amount = 80.0, date = "2024-11-10", time = "10:20:00", category_id = 1),
    Expense(amount = 190.0, date = "2024-11-12", time = "11:30:00", category_id = 2),
    Expense(amount = 220.0, date = "2024-11-14", time = "12:40:00", category_id = 3),
    Expense(amount = 140.0, date = "2024-12-10", time = "13:50:00", category_id = 4),
    Expense(amount = 60.0, date = "2024-12-12", time = "14:15:00", category_id = 5),
    Expense(amount = 85.0, date = "2024-11-11", time = "15:25:00", category_id = 6),
    Expense(amount = 155.0, date = "2024-11-13", time = "16:35:00", category_id = 1),
    Expense(amount = 180.0, date = "2024-12-14", time = "17:45:00", category_id = 2),
    Expense(amount = 230.0, date = "2024-12-16", time = "18:55:00", category_id = 3)
)

val defaultIncome = listOf(
    Income(amount = 500.0, date = "2024-11-12", time = "12:30:30", source = "Salary", description = "Received salary from boss"),
    Income(amount = 600.0, date = "2024-11-13", time = "08:00:00", source = "Bonus", description = "Performance bonus"),
    Income(amount = 800.0, date = "2024-11-14", time = "09:30:00", source = "Freelance", description = "Freelance project"),
    Income(amount = 200.0, date = "2024-11-15", time = "11:00:00", source = "Gift", description = "Gift from family"),
    Income(amount = 300.0, date = "2024-11-16", time = "10:00:00", source = "Part-time Job", description = "Part-time tutoring"),
    Income(amount = 750.0, date = "2024-11-12", time = "14:00:00", source = "Rent", description = "Rental income"),
    Income(amount = 600.0, date = "2024-11-13", time = "15:30:00", source = "Stock Dividends", description = "Quarterly dividends"),
    Income(amount = 1200.0, date = "2024-11-14", time = "16:45:00", source = "Salary", description = "Monthly salary"),
    Income(amount = 400.0, date = "2024-11-15", time = "17:00:00", source = "Bonus", description = "Festival bonus"),
    Income(amount = 500.0, date = "2024-11-16", time = "18:15:00", source = "Savings", description = "Interest income"),
    Income(amount = 300.0, date = "2024-12-12", time = "19:30:00", source = "Salary", description = "Extra shift"),
    Income(amount = 450.0, date = "2024-12-13", time = "20:45:00", source = "Bonus", description = "Target achievement"),
    Income(amount = 200.0, date = "2024-12-14", time = "21:00:00", source = "Gift", description = "From a friend"),
    Income(amount = 800.0, date = "2024-12-15", time = "22:15:00", source = "Freelance", description = "Side project"),
    Income(amount = 900.0, date = "2024-12-16", time = "23:30:00", source = "Salary", description = "Overtime pay"),
    Income(amount = 550.0, date = "2024-11-10", time = "12:20:00", source = "Freelance", description = "Logo design project"),
    Income(amount = 450.0, date = "2024-11-12", time = "13:30:00", source = "Salary", description = "Consulting salary"),
    Income(amount = 500.0, date = "2024-11-14", time = "14:40:00", source = "Savings", description = "Bank interest"),
    Income(amount = 400.0, date = "2024-12-10", time = "15:50:00", source = "Gift", description = "Anniversary gift"),
    Income(amount = 700.0, date = "2024-12-12", time = "16:15:00", source = "Part-time Job", description = "Teaching income"),
    Income(amount = 650.0, date = "2024-11-11", time = "17:25:00", source = "Bonus", description = "Performance bonus"),
    Income(amount = 750.0, date = "2024-11-13", time = "18:35:00", source = "Rent", description = "Rental income"),
    Income(amount = 900.0, date = "2024-12-14", time = "19:45:00", source = "Freelance", description = "Website design"),
    Income(amount = 1000.0, date = "2024-12-16", time = "20:55:00", source = "Stock Dividends", description = "Annual dividend")
)

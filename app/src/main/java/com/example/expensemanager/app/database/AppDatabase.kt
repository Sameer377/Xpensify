package com.example.expensemanager.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Category::class, Income::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun incomeDao(): IncomeDao

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

                // Insert default income
                getDatabase(context).incomeDao().insertIncome(defaultIncome)
            }
        }
    }
}

val defaultCategories = listOf(
    Category(name = "Home", icon = "home_icon", color = "#FF5733"),
    Category(name = "Entertainment", icon = "entertainment_icon", color = "#2196F3"),
    Category(name = "Traveling", icon = "travel_icon", color = "#4CAF50"),
    Category(name = "Clothes", icon = "clothes_icon", color = "#FFC107"),
    Category(name = "Sports", icon = "sports_icon", color = "#9C27B0")
)

val defaultIncome = listOf(
    Income(amount = 0.0, date = "12 Dec 2024", source = "Salary", description = "Received salary from boss")
)

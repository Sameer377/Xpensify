package com.example.expensemanager.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

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
                    .addCallback(DatabaseCallback(context)) // Attach the callback
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback for prepopulating default categories
    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Insert default categories in a background thread
            CoroutineScope(Dispatchers.IO).launch {
                getDatabase(context).categoryDao().insertCategories(defaultCategories)
            }
        }
    }
}

// Default categories for prepopulation
val defaultCategories = listOf(
    Category(name = "Home", icon = "home_icon", color = "#FF5733"),
    Category(name = "Entertainment", icon = "entertainment_icon", color = "#2196F3"),
    Category(name = "Traveling", icon = "travel_icon", color = "#4CAF50"),
    Category(name = "Clothes", icon = "clothes_icon", color = "#FFC107"),
    Category(name = "Sports", icon = "sports_icon", color = "#9C27B0")
)

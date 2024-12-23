package com.example.expensemanager.app.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: Int = 957,
    val color: String? = null // Optional field for storing color in HEX format
)

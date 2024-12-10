package com.example.expensemanager.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.expensemanager.app.adapters.CategoryAdapter
import com.example.expensemanager.app.adapters.OnlyCategoryAdapter
import com.example.expensemanager.app.database.AppDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCategories : AppCompatActivity() {

    private lateinit var listView: ListView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_categories)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var ImageView = findViewById<ImageView>(R.id.back_btn)

        ImageView.setOnClickListener{
            finish()
        }


        var btn = findViewById<FloatingActionButton>(R.id.categories_float_btn)



        btn.setOnClickListener{
           startActivity( Intent(this, NewCategory::class.java))
        }

        initUi()
    }

    private fun initUi() {
        listView = findViewById<ListView>(R.id.ExpenseCategoryListView)
        loadCategories()
    }

    private fun loadCategories() {
        // Use coroutine to fetch data from Room database
        lifecycleScope.launch(Dispatchers.IO) {
            val categoryDao = AppDatabase.getDatabase(this@EditCategories).categoryDao()
            val categories = categoryDao.getAllCategories()

            withContext(Dispatchers.Main) {
                val adapter = OnlyCategoryAdapter(this@EditCategories, categories)
                listView.adapter = adapter
            }
        }
    }


}
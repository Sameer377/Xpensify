package com.example.expensemanager.app

import android.annotation.SuppressLint
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class NewCategory : AppCompatActivity() {

    private lateinit var editTextCategory: EditText
    private lateinit var buttonAddCategory: Button
    private lateinit var selectedicon: ImageView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val icon_btn: CardView = findViewById<CardView>(R.id.icon_btn)
        icon_btn.setOnClickListener {

        }


        var btn = findViewById<ImageView>(R.id.back_btn)

        btn.setOnClickListener{
            finish()
        }

        initUi()

    }

    private fun initUi() {
        editTextCategory = findViewById(R.id.category_edit_text)
        buttonAddCategory = findViewById(R.id.create_category_btn)
        selectedicon = findViewById<ImageView>(R.id.selected_icon)

      /*  buttonAddCategory.setOnClickListener {
            val categoryName = editTextCategory.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                addCategory(categoryName)
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }*/

        buttonAddCategory.setOnClickListener {
            val categoryName = editTextCategory.text.toString().trim()

            if (categoryName.isNotEmpty()) {
                if (selectedIcons!=0) {
                    // Pass category name and selected icon IDs
                    addCategory(categoryName, selectedIcons)
                } else {
                    Toast.makeText(this, "Please select at least one icon", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

    }



    private var selectedIcons: Int = 0



    private fun addCategory(categoryName: String, icon: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val categoryDao = AppDatabase.getDatabase(this@NewCategory).categoryDao()

            // Insert new category
            val newCategory = Category(name = categoryName, icon = icon)
            categoryDao.insert(newCategory)

            // Reload categories
            val categories = categoryDao.getAllCategories()

            withContext(Dispatchers.Main) {


                // Clear the EditText and show a success message
                editTextCategory.text.clear()
                Toast.makeText(this@NewCategory, "Category added", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
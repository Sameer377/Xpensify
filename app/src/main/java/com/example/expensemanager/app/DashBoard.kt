package com.example.expensemanager.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.Category
import com.example.expensemanager.app.dialogs.TransactionBottomSheet
import com.example.expensemanager.app.fragments.Categories
import com.example.expensemanager.app.fragments.Transactions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class DashBoard : AppCompatActivity() {

    // Declare BottomNavigationView as a global variable
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var categories: AppCompatButton
    private lateinit var transaction: AppCompatButton
    private lateinit var floatingActionButton:  FloatingActionButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUi()
        initListener()
    }

    private fun initUi() {

        categories = findViewById<AppCompatButton>(R.id.tab_categories)
        transaction = findViewById<AppCompatButton>(R.id.tab_transaction)
        floatingActionButton = findViewById<FloatingActionButton>(R.id.dash_floating_btn);

        initCategories()

        changeToTransactions()
    }

    private fun initCategories() {
        AppDatabase.getDatabase(this)
        fetchAndDisplayCategories()
    }

    private fun fetchAndDisplayCategories() {
        // Launch a coroutine on the IO thread to fetch data
        CoroutineScope(Dispatchers.IO).launch {
            val categoryDao = AppDatabase.getDatabase(baseContext ).categoryDao()
            val categories = categoryDao.getAllCategories()

            // Prepare the data to show in the Toast
            val categoryNames = categories.joinToString { it.name }

            // Switch to the main thread to show the Toast
            withContext(Dispatchers.Main) {
                Toast.makeText(baseContext, "Categories: $categoryNames", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun initListener() {


        categories.setOnClickListener{
            changeToCategories()
            Toast.makeText(this,"add",Toast.LENGTH_SHORT).show()
        }

        transaction.setOnClickListener{
            changeToTransactions()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)



        navigationView.setNavigationItemSelectedListener { menuItem ->

            drawerLayout.closeDrawers()
            true
        }

        var bmenu = findViewById<ImageView>(R.id.btn_burger_menu)

        bmenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }

    public fun changeToCategories(){
       categories.setBackgroundResource(R.drawable.filled_btn_rounded);
        categories .setTextColor(ContextCompat.getColor(this, R.color.white))

        transaction.setBackgroundResource(R.drawable.outline_rounded_btn);
        transaction .setTextColor(ContextCompat.getColor(this, R.color.primary_dark_black_dark_1))

        floatingActionButton.setImageResource(R.drawable.ic_edit_light)

        floatingActionButton.setOnClickListener {
            val intent = Intent(this, EditCategories::class.java)
            startActivity(intent)
        }


        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.dash_frame, Categories())

        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()
    }

    public fun changeToTransactions(){
        categories.setBackgroundResource(R.drawable.outline_rounded_btn);
        categories .setTextColor(ContextCompat.getColor(this, R.color.primary_dark_black_dark_1))

        transaction.setBackgroundResource(R.drawable.filled_btn_rounded);
        transaction .setTextColor(ContextCompat.getColor(this, R.color.white))

        floatingActionButton.setImageResource(R.drawable.ic_add_light)


        val bottomSheetFragment = TransactionBottomSheet()



        floatingActionButton.setOnClickListener {
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }


        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.dash_frame, Transactions())

        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()
    }


}

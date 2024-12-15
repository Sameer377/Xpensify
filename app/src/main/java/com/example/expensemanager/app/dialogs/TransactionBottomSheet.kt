package com.example.expensemanager.app.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.app.EditCategories
import com.example.expensemanager.app.R
import com.example.expensemanager.app.adapters.HorizontalAdapter
import com.example.expensemanager.app.adapters.OnlyCategoryAdapter
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.fragments.Categories
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var categoryList: RecyclerView
    private lateinit var expense: AppCompatButton
    private lateinit var income: AppCompatButton
    private lateinit var amountedt: AppCompatEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v = inflater.inflate(R.layout.transaction_bottom_sheet_dialog, container, false)

        var e = v.findViewById<AppCompatEditText>(R.id.bottom_sheet_amount)

        e.requestFocus()



        initUi(v)

        return v
    }

    private fun initUi(v:View) {
        categoryList = v.findViewById<RecyclerView>(R.id.cateogry_list_bottom_sheet)
        loadCategories()


        expense = v.findViewById<AppCompatButton>(R.id.tab_expense)
        income = v.findViewById<AppCompatButton>(R.id.tab_income)
        amountedt = v.findViewById<AppCompatEditText>(R.id.bottom_sheet_amount)

        changeToExpense()

        expense.setOnClickListener{
            changeToExpense()
        }

        income.setOnClickListener{
            changeToIncome()
        }
    }

    private fun loadCategories() {
        // Use coroutine to fetch data from Room database
        lifecycleScope.launch(Dispatchers.IO) {
            val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()
            val categories = categoryDao.getAllCategories()

            withContext(Dispatchers.Main) {
                val adapter = HorizontalAdapter(requireContext(), categories)
                val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                categoryList.layoutManager = layoutManager
                categoryList.adapter = adapter
            }
        }

    }


    public fun changeToExpense(){
        expense.setBackgroundResource(R.drawable.filled_btn_rounded);
        expense .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        income.setBackgroundResource(R.drawable.outline_rounded_btn);
        income .setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_dark_black_dark_1))

        categoryList.visibility =View.VISIBLE
    }

    public fun changeToIncome(){
        income.setBackgroundResource(R.drawable.filled_btn_rounded);
        income .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        expense.setBackgroundResource(R.drawable.outline_rounded_btn);
        expense .setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_dark_black_dark_1))

        categoryList.visibility =View.GONE


    }


    override fun onResume() {
        super.onResume()
        loadCategories()
    }


}
package com.example.expensemanager.app.dialogs

import android.content.Context
import android.content.DialogInterface
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
import com.example.expensemanager.app.TransactionBottomSheetListener
import com.example.expensemanager.app.adapters.HorizontalAdapter
import com.example.expensemanager.app.adapters.OnlyCategoryAdapter
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.AppDatabase.Companion.getDatabase
import com.example.expensemanager.app.database.Expense
import com.example.expensemanager.app.database.Income
import com.example.expensemanager.app.database.IncomeDao
import com.example.expensemanager.app.fragments.Categories
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TransactionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var categoryList: RecyclerView
    private lateinit var expense: AppCompatButton
    private lateinit var income: AppCompatButton
    private lateinit var add_btn: AppCompatButton
    private lateinit var amountedt: AppCompatEditText
    private lateinit var sourceedt: AppCompatEditText

    private var onDismissListener: (() -> Unit)? = null

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
        add_btn = v.findViewById<AppCompatButton>(R.id.add_bottom_sheet_btn)
        amountedt = v.findViewById<AppCompatEditText>(R.id.bottom_sheet_amount)
        sourceedt = v.findViewById<AppCompatEditText>(R.id.source)

        changeToExpense()

        expense.setOnClickListener{
            changeToExpense()
        }

        income.setOnClickListener{
            changeToIncome()
        }
    }

    private var category_id : Int = 0

    private fun loadCategories() {
        // Use coroutine to fetch data from Room database
        lifecycleScope.launch(Dispatchers.IO) {
            val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()
            val categories = categoryDao.getAllCategories()

            withContext(Dispatchers.Main) {
                val adapter = HorizontalAdapter(requireContext(), categories) { position ->
                    val clickedItem = categories[position]
                    Toast.makeText(requireContext(), "Clicked item at position: $position", Toast.LENGTH_SHORT).show()
                    category_id=position+1
                }

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
        sourceedt.visibility=View.GONE

        add_btn.setOnClickListener{
            addExpense()
        }
    }

    public fun changeToIncome(){
        income.setBackgroundResource(R.drawable.filled_btn_rounded);
        income .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        expense.setBackgroundResource(R.drawable.outline_rounded_btn);
        expense .setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_dark_black_dark_1))

        categoryList.visibility =View.GONE
        sourceedt.visibility=View.VISIBLE

        add_btn.setOnClickListener{
            addIncome()
        }

    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = Date()
        return timeFormat.format(time)
    }



    public  fun addIncome(){

        val am : Double? = amountedt.text.toString().trim().toDoubleOrNull()
        val sc = sourceedt.text.toString().trim()
        val income = am?.let {
            Income(
                amount = it,
                date = getCurrentDate(),
                time = getCurrentTime(),
                source = sc,
                description = sc,
            )
        }


        if (income != null) {
            // Use coroutine to insert the record in the database on a background thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Get the DAO and insert the income record
                    val incomeDao = context?.let { AppDatabase.getDatabase(it).incomeDao() }
                    incomeDao?.insertIncome(income)

                    // Show success message on the main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Income added successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Handle error if insertion fails
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Error adding income: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
        }

    }


 public  fun addExpense(){

        if(category_id==0){
            Toast.makeText(context,"Select Category",Toast.LENGTH_SHORT).show()
            return
        }

        val am : Double? = amountedt.text.toString().trim().toDoubleOrNull()
//        val sc = sourceedt.text.toString().trim()
        val expense = am?.let {
            Expense(
                amount = it,
                date = getCurrentDate(),
                time = getCurrentTime(),
                category_id = category_id,
                note = "food"

            )
        }


        if (expense != null) {
            // Use coroutine to insert the record in the database on a background thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Get the DAO and insert the income record
                    val expenseDao = context?.let { AppDatabase.getDatabase(it).expenseDao() }
                    expenseDao?.insertExpense(expense)

                    // Show success message on the main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Income added successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Handle error if insertion fails
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Error adding income: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TransactionBottomSheetListener) {
            onDismissListener = { context.onBottomSheetClosed() }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
    }


}
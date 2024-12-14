package com.example.expensemanager.app.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.app.R
import com.example.expensemanager.app.adapters.HorizontalAdapter
import com.example.expensemanager.app.adapters.OnlyCategoryAdapter
import com.example.expensemanager.app.database.AppDatabase
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var categoryList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v = inflater.inflate(R.layout.transaction_bottom_sheet_dialog, container, false)

        var e = v.findViewById<AppCompatEditText>(R.id.bottom_sheet_edit1)

        e.requestFocus()



        initUi(v)

        return v
    }

    private fun initUi(v:View) {
        categoryList = v.findViewById<RecyclerView>(R.id.cateogry_list_bottom_sheet)
        loadCategories()
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

    override fun onResume() {
        super.onResume()
        loadCategories()
    }

}
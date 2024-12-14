package com.example.expensemanager.app.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.expensemanager.app.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.adapters.CategoryAdapter

class Categories : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var listView: ListView


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        pieChart = view.findViewById(R.id.pieChart)
        setupPieChart()

       /* val imageView: ImageView = view.findViewById(R.id.gifImageView)

        // Load GIF into ImageView
        Glide.with(this)
            .asGif()
            .load(R.drawable.asset_allocation_unscreen) // Replace with your GIF file
            .into(imageView)*/

        val filterbtn: ImageView = view.findViewById(R.id.filter_menu)

        // Set up popup menu for the ImageView
        filterbtn.setOnClickListener { showPopupMenu(it) }

        listView = view.findViewById<ListView>(R.id.categoryListView)

        loadCategories()

        return view
    }


    private fun loadCategories() {
        // Use coroutine to fetch data from Room database
        lifecycleScope.launch(Dispatchers.IO) {
            val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()
            val categories = categoryDao.getAllCategories()

            withContext(Dispatchers.Main) {
                val adapter = CategoryAdapter(requireContext(), categories)
                listView.adapter = adapter
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.popup_menu) // Ensure you have created a `res/menu/popup_menu.xml`

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_day -> {
                    Toast.makeText(requireContext(), "Day selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_week -> {
                    Toast.makeText(requireContext(), "Week selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_month -> {
                    Toast.makeText(requireContext(), "Month selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_year -> {
                    Toast.makeText(requireContext(), "Year selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun setupPieChart() {
        val entries = listOf(
            PieEntry(40f, "Food"),
            PieEntry(20f, "Rent"),
            PieEntry(15f, "Transport"),
            PieEntry(25f, "Others")
        )

        val dataSet = PieDataSet(entries, "Expense Categories").apply {
            colors = listOf(
                Color.rgb(244, 67, 54), // Red
                Color.rgb(33, 150, 243), // Blue
                Color.rgb(76, 175, 80), // Green
                Color.rgb(255, 193, 7)  // Yellow
            )
            valueTextSize = 12f
            valueTextColor = Color.WHITE
        }

        val pieData = PieData(dataSet)
        pieChart.data = pieData

        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setCenterTextSize(18f)
            animateY(1000)
            legend.isEnabled = false
        }

        pieChart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
    }
}

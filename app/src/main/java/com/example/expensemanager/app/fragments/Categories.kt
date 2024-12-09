package com.example.expensemanager.app.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.expensemanager.app.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class Categories : Fragment() {

    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        // Initialize the PieChart
        pieChart = view.findViewById(R.id.pieChart)
        setupPieChart()

        return view
    }

    private fun setupPieChart() {
        // Sample data
        val entries = listOf(
            PieEntry(40f, "Food"),
            PieEntry(20f, "Rent"),
            PieEntry(15f, "Transport"),
            PieEntry(25f, "Others")
        )

        // Configure PieDataSet
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

        // Set data to the PieChart
        val pieData = PieData(dataSet)
        pieChart.data = pieData

        // Configure PieChart appearance
        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            transparentCircleRadius = 59f
            centerText = "Expenses"
            setCenterTextSize(10f)
            animateY(1000)
            pieChart.legend.textSize=8f
        }

        // Refresh the chart
        pieChart.invalidate()
    }
}

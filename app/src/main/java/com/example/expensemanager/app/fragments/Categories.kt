package com.example.expensemanager.app.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.expensemanager.app.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class Categories : Fragment() {

    private lateinit var pieChart: PieChart

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        pieChart = view.findViewById(R.id.pieChart)
        setupPieChart()


        val imageView: ImageView = view.findViewById(R.id.gifImageView)
        Glide.with(this)
            .asGif()
            .load(R.drawable.anim_logo) // Replace with your GIF file
            .into(imageView)


        return view
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
        pieChart.legend.isEnabled=false

        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            transparentCircleRadius = 61f
           
            setCenterTextSize(18f)
            animateY(1000)
            pieChart.legend.textSize=8f

        }

        pieChart.invalidate()
    }
}

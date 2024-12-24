package com.example.expensemanager.app.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.example.expensemanager.app.database.CategoryData
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Categories : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var listView: ListView
    private lateinit var date : TextView
    private lateinit var inex : TextView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)

        pieChart = view.findViewById(R.id.pieChart)
        date = view.findViewById(R.id.date_category)
        inex = view.findViewById(R.id.inex)
        date.text = formatDate(getCurrentDate())
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
            var totalExpense = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalExpense() }
            var totalIncome = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalIncome() }
            val categoryDao = AppDatabase.getDatabase(requireContext()).transactionDao()
            val categories = categoryDao.getCategoryData()

            withContext(Dispatchers.Main) {
                if(totalExpense!=null){
                    val adapter = CategoryAdapter(requireContext(), categories,totalExpense)
                    listView.adapter = adapter
                    setupPieChart(categories,totalExpense)
                    inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()
                }else{
                    val t = totalExpense
                    if(t!=null){
                        val adapter = CategoryAdapter(requireContext(), categories,t)
                        listView.adapter = adapter
                        setupPieChart(categories,t)

                    }
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }

    private fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            Log.e("FormatDate", "Invalid date format: $inputDate", e)
            ""
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.popup_menu) // Ensure you have created a `res/menu/popup_menu.xml`

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_all -> {
                    date.text = formatDate(getCurrentDate())

                    lifecycleScope.launch(Dispatchers.IO) {
                        var totalExpense = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalExpense() }
                        var totalIncome = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalIncome() }

                        val categoryDao = AppDatabase.getDatabase(requireContext()).transactionDao()
                        val categories = categoryDao.getCategoryData()

                        withContext(Dispatchers.Main) {
                            if(totalExpense!=null){
                                val adapter = CategoryAdapter(requireContext(), categories,totalExpense)
                                listView.adapter = adapter
                                setupPieChart(categories,totalExpense)
                                inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                            }else{
                                val t = totalExpense
                                if(t!=null){
                                    val adapter = CategoryAdapter(requireContext(), categories,t)
                                    listView.adapter = adapter
                                    setupPieChart(categories,t)
                                    inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                                }
                            }
                        }
                    }
                    true
                }
                R.id.action_day -> {
                    showDatePicker { selectedDate ->
                        date.text = formatDate(selectedDate)

                        lifecycleScope.launch(Dispatchers.IO) {
                            var totalExpense = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalExpenseOfTheDate(selectedDate) }
                            val categoryDao = AppDatabase.getDatabase(requireContext()).transactionDao()
                            val categories = categoryDao.getCategoryData()
                            var totalIncome = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalIncomeOfTheDate(selectedDate) }

                            withContext(Dispatchers.Main) {
                                if(totalExpense!=null){
                                    val adapter = CategoryAdapter(requireContext(), categories,totalExpense)

                                    setupPieChart(categories,totalExpense)
                                    listView.adapter = adapter
                                    inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                                }else{
                                    val t = totalExpense
                                    if(t!=null){
                                        val adapter = CategoryAdapter(requireContext(), categories,t)
                                        listView.adapter = adapter
                                        setupPieChart(categories,t)
                                        inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                                    }
                                }
                            }
                        }
                        Toast.makeText(requireContext(), "Day selected: $selectedDate", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.action_week -> {
                    showDatePicker { startDate ->
                        weeklyTransaction(startDate)
                    }
                    true
                }
                R.id.action_month -> {
                    showDatePicker { startDate ->
                        monthlyTransaction(startDate)
                    }
                    true
                }
                R.id.action_year -> {
                    showDatePicker { startDate ->
                        yearlyTransaction(startDate)
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun weeklyTransaction(startDate: String) {
        val dateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        try {
            // Parse the start date
            calendar.time = dateFormat.parse(startDate)!!

            // Add 6 days to get the end date
            calendar.add(Calendar.DAY_OF_YEAR, 6)

            val endDate = dateFormat.format(calendar.time)
            date.text = formatDate(startDate)+" to "+formatDate(endDate)
            // Call the range function with the start and end dates
            lifecycleScope.launch(Dispatchers.IO) {
                var totalExpense = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalExpenseInRange(startDate,endDate) }
                var totalIncome = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalIncomeInRange(startDate,endDate) }
                val categoryDao = AppDatabase.getDatabase(requireContext()).transactionDao()
                val categories = categoryDao.getCategoryData()
                listView.removeAllViews()
                withContext(Dispatchers.Main) {
                    if(totalExpense!=null){
                        val adapter = CategoryAdapter(requireContext(), categories,totalExpense)
                        listView.adapter = adapter
                        setupPieChart(categories,totalExpense)
                        inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                    }else{
                        val t = totalExpense
                        if(t!=null){
                            val adapter = CategoryAdapter(requireContext(), categories,t)
                            listView.adapter = adapter
                            setupPieChart(categories,t)
                            inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                        }
                    }
                }
            }
            // Process or display the transactions as needed

        } catch (e: Exception) {
            Log.e("WeeklyTransaction", "Invalid start date format: $startDate", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthlyTransaction(startDate: String) {
        val dateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        try {
            // Parse the start date
            calendar.time = dateFormat.parse(startDate)!!

            // Add 6 days to get the end date
            calendar.add(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endDate = dateFormat.format(calendar.time)
            date.text = formatDate(startDate)+" to "+formatDate(endDate)

            // Call the range function with the start and end dates
            lifecycleScope.launch(Dispatchers.IO) {
                var totalExpense = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalExpenseInRange(startDate,endDate) }
                var totalIncome = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalIncomeInRange(startDate,endDate) }

                val categoryDao = AppDatabase.getDatabase(requireContext()).transactionDao()
                val categories = categoryDao.getCategoryData()

                withContext(Dispatchers.Main) {
                    if(totalExpense!=null){
                        val adapter = CategoryAdapter(requireContext(), categories,totalExpense)
                        listView.adapter = adapter
                        setupPieChart(categories,totalExpense)
                        inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                    }else{
                        val t = totalExpense
                        if(t!=null){
                            val adapter = CategoryAdapter(requireContext(), categories,t)
                            listView.adapter = adapter
                            setupPieChart(categories,t)
                            inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                        }
                    }
                }
            }

            // Process or display the transactions as needed

        } catch (e: Exception) {
            Log.e("WeeklyTransaction", "Invalid start date format: $startDate", e)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun yearlyTransaction(startDate: String) {
        val dateFormat =
            SimpleDateFormat("yyyy-M-d", Locale.getDefault()) // Adjusted to match the requirement
        val calendar = Calendar.getInstance()

        try {
            // Parse the start date
            calendar.time = dateFormat.parse(startDate)!!

            // Add 6 days to get the end date
            calendar.add(Calendar.DAY_OF_YEAR, 365)

            val endDate = dateFormat.format(calendar.time)
            date.text = formatDate(startDate)+" to "+formatDate(endDate)
            lifecycleScope.launch(Dispatchers.IO) {
                var totalExpense = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalExpenseInRange(startDate,endDate) }
                val categoryDao = AppDatabase.getDatabase(requireContext()).transactionDao()
                val categories = categoryDao.getCategoryData()
                var totalIncome = context?.let { AppDatabase.getDatabase(it).transactionDao().getTotalIncomeInRange(startDate,endDate) }

                withContext(Dispatchers.Main) {
                    if(totalExpense!=null){
                        val adapter = CategoryAdapter(requireContext(), categories,totalExpense)
                        listView.adapter = adapter
                        setupPieChart(categories,totalExpense)
                        inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                    }else{
                        val t = totalExpense
                        if(t!=null){
                            val adapter = CategoryAdapter(requireContext(), categories,t)
                            listView.adapter = adapter
                            setupPieChart(categories,t)
                            inex.text = "-"+totalExpense.toString()+"\n"+"+"+totalIncome.toString()

                        }
                    }
                }
            }

            // Call the range function with the start and end dates

            // Process or display the transactions as needed
        } catch (e: Exception) {
            Log.e(
                "YearlyTransaction",
                "Invalid start date format: $startDate",
                e
            ) // Corrected log tag

            Toast.makeText(requireContext(),e.printStackTrace().toString(),Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Construct the selected date
                val selectedDate = "${selectedYear}-${selectedMonth + 1}-$selectedDay"
                onDateSelected(selectedDate) // Pass the selected date to the callback
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun setupPieChart(categories:List<CategoryData>,totalExpense:Double) {
        val entries = categories.map { category ->
            val percentage = (category.amount / (totalExpense / 100)).toFloat()
            PieEntry(percentage, category.category_name)
        }

        val dataSet = PieDataSet(entries, "Expense Categories").apply {
            colors = categories.map { category ->
                category.categoryColor?.let { Color.parseColor(it) } ?: Color.rgb(200, 200, 200)
            }
            valueTextSize = 12f
            valueTextColor = Color.WHITE
        }

        val pieData = PieData(dataSet).apply {
            setValueTextSize(12f)
            setValueTextColor(Color.WHITE)
            setValueFormatter(PercentFormatter())
        }

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

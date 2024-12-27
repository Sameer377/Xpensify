package com.example.expensemanager.app.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.app.R
import com.example.expensemanager.app.adapters.TransactionAdapter
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.TransactionViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import java.io.File

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.itextpdf.layout.element.Cell

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.expensemanager.app.database.Transaction

class Transactions : Fragment() {


    private lateinit var balanceTv: TextView
    private lateinit var income: TextView
    private lateinit var expense: TextView
    private lateinit var seeall: TextView
    private lateinit var transactionDate: TextView
    private lateinit var viewModel: TransactionViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var  recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var view  :  View

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val v:View =   inflater.inflate(R.layout.fragment_transactions, container, false)

        expense = v.findViewById<TextView>(R.id.expense_transaction_card)
        balanceTv = v.findViewById<TextView>(R.id.balance_transaction_card)
        transactionDate = v.findViewById<TextView>(R.id.transactionDate)
        income = v.findViewById<TextView>(R.id.income_transaction_card)
        seeall = v.findViewById<TextView>(R.id.filter_menu_transaction)
        recyclerView = v.findViewById(R.id.recyclerView_transaction)



        initUi()
        view=v


        return v
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUi() {
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
       initCard(0,"","")

        transactionDate.text = formatDate(getCurrentDate())




        // Setup RecyclerView

        transactionAdapter = TransactionAdapter(requireContext(),emptyList())
        recyclerView.adapter = transactionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe data from ViewModel
         viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
             view.findViewById<AppCompatButton>(R.id.download_report).setOnClickListener {
                 createFinancialSummaryPdf(transactions,"all","all",income.text.toString(),expense.text.toString(),balanceTv.text.toString())
             }

             transactionAdapter.updateData(transactions)

        }

        // Fetch transactions
        viewModel.fetchTransactions()


        seeall.setOnClickListener { showPopupMenu(it) }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCard(action_id:Int, startDate: String?, endDate:String?){


        when(action_id){

            0-> {
                
                CoroutineScope(Dispatchers.IO).launch {
                    val totalIncome = context?.let { AppDatabase.getDatabase(it).incomeDao().getTotalIncome() }
                    val totalExpense = context?.let { AppDatabase.getDatabase(it).expenseDao().getTotalExpenses() }
                    var balance : Double = 0.0
                    CoroutineScope(Dispatchers.Main).launch {
                        if (totalIncome != null) {
                            income.text = totalIncome.toString()
                            balance=balance+totalIncome.toDouble()
                        }
                        if (totalExpense != null) {
                            expense.text = totalExpense.toString()
                            balance=balance-totalExpense.toDouble()

                            balanceTv.text = balance.toString()
                        }
                    }

                }

                viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                    view.findViewById<AppCompatButton>(R.id.download_report).setOnClickListener {
                        createFinancialSummaryPdf(transactions,getCurrentDate(),getCurrentDate(),income.text.toString(),expense.text.toString(),balanceTv.text.toString())

                    }


                }
                true
            }

             R.id.action_all -> {
                
                CoroutineScope(Dispatchers.IO).launch {
                    val totalIncome = context?.let { AppDatabase.getDatabase(it).incomeDao().getTotalIncome() }
                    val totalExpense = context?.let { AppDatabase.getDatabase(it).expenseDao().getTotalExpenses() }
                    var balance : Double = 0.0
                    CoroutineScope(Dispatchers.Main).launch {
                        if (totalIncome != null) {
                            income.text = totalIncome.toString()
                            balance=balance+totalIncome.toDouble()
                        }else{
                            income.text = "0.00"
                        }
                        if (totalExpense != null) {
                            expense.text = totalExpense.toString()
                            balance=balance-totalExpense.toDouble()

                            balanceTv.text = balance.toString()
                        }else{
                            income.text = "0.00"
                        }

                        if (totalIncome == null && totalExpense == null){
                            expense.text ="0.00"
                            income.text = "0.00"
                            balanceTv.text = "0.00"
                        }
                    }

                }
                 viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                     view.findViewById<AppCompatButton>(R.id.download_report).setOnClickListener {
                         createFinancialSummaryPdf(transactions,getCurrentDate(),getCurrentDate(),income.text.toString(),expense.text.toString(),balanceTv.text.toString())
                     }
                 }
                true
            }
            R.id.action_day -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val totalIncome = context?.let { AppDatabase.getDatabase(it).incomeDao().getTotalIncomeOfDate(startDate.toString()) }
                    val totalExpense = context?.let { AppDatabase.getDatabase(it).expenseDao().getTotalExpenseOfDate(startDate.toString()) }
                    var balance : Double = 0.0
                    CoroutineScope(Dispatchers.Main).launch {
                        if (totalIncome != null) {
                            income.text = totalIncome.toString()
                            balance=balance+totalIncome.toDouble()
                        }else{
                            income.text = "0.00"
                        }
                        if (totalExpense != null) {
                            expense.text = totalExpense.toString()
                            balance=balance-totalExpense.toDouble()

                            balanceTv.text = balance.toString()
                        }else{
                            income.text = "0.00"
                        }

                        if (totalIncome == null && totalExpense == null){
                            expense.text ="0.00"
                            income.text = "0.00"
                            balanceTv.text = "0.00"
                        }
                    }

                }
                viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                    view.findViewById<AppCompatButton>(R.id.download_report).setOnClickListener {
                        createFinancialSummaryPdf(transactions,startDate.toString(),endDate.toString(),income.text.toString(),expense.text.toString(),balanceTv.text.toString())
                    }


                }
                true
            }
            R.id.action_week  -> {



                CoroutineScope(Dispatchers.IO).launch {
                    val totalIncome = context?.let { AppDatabase.getDatabase(it).incomeDao().getTotalIncomeInRange(startDate.toString(),
                        endDate.toString()
                    ) }
                    val totalExpense = context?.let { AppDatabase.getDatabase(it).expenseDao().getTotalExpenseInRange(startDate.toString(),endDate.toString()) }
                    var balance : Double = 0.0
                    CoroutineScope(Dispatchers.Main).launch {
                        if (totalIncome != null) {
                            income.text = totalIncome.toString()
                            balance=balance+totalIncome.toDouble()
                        }else{
                            income.text = "0.00"
                        }
                        if (totalExpense != null) {
                            expense.text = totalExpense.toString()
                            balance=balance-totalExpense.toDouble()

                            balanceTv.text = balance.toString()
                        }else{
                            income.text = "0.00"
                        }

                        if (totalIncome == null && totalExpense == null){
                            expense.text ="0.00"
                            income.text = "0.00"
                            balanceTv.text = "0.00"
                        }
                    }

                }
                viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
                    view.findViewById<AppCompatButton>(R.id.download_report).setOnClickListener {
                        createFinancialSummaryPdf(transactions,startDate.toString(),endDate.toString(),income.text.toString(),expense.text.toString(),balanceTv.text.toString())
                    }


                }
                true
            }
            else ->{
                Toast.makeText(requireContext(),"error",Toast.LENGTH_SHORT).show()
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.popup_menu) // Ensure you have created a `res/menu/popup_menu.xml`

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_all -> {
                    initCard(R.id.action_all,"","")
                    transactionDate.text = formatDate(getCurrentDate())
                    viewModel.fetchTransactions()
                    Toast.makeText(requireContext(), "Day selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_day -> {
                    showDatePicker { selectedDate ->
                        initCard(R.id.action_day,selectedDate,"")
                        transactionDate.text = formatDate(selectedDate)
                        viewModel.fetchDayTransactions(selectedDate)
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

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
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
            transactionDate.text = formatDate(startDate)+" to "+formatDate(endDate)
            initCard(R.id.action_week,startDate,endDate)
            // Call the range function with the start and end dates
            viewModel.fetchTransactionsInRange(startDate, endDate)

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
            transactionDate.text = formatDate(startDate)+" to "+formatDate(endDate)
            initCard(R.id.action_week,startDate,endDate)

            // Call the range function with the start and end dates
            viewModel.fetchTransactionsInRange(startDate, endDate)

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
            transactionDate.text = formatDate(startDate)+" to "+formatDate(endDate)
            initCard(R.id.action_week,startDate,endDate)

            // Call the range function with the start and end dates
            viewModel.fetchTransactionsInRange(startDate, endDate)

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
    fun createFinancialSummaryPdf(transactions: List<Transaction>,startDate: String?,endDate: String?,income:String?,expense:String?,balance:String?) {

        checkStoragePermission(requireContext())
        // Ensure the storage directory exists
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val fileName = "FinancialSummary.pdf"
        val file = File(path, fileName)

        // Check if the directory exists, and create it if necessary
        val directory = File(path)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        try {
            // Create the PDF writer and document
            val pdfWriter = PdfWriter(file)
            val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Add title
            val title = Paragraph("Xpensify").setFontSize(24f).setBold()
            val description = Paragraph("Your Smart Financial Tracker\nEasily manage and track your income and expenses.").setFontSize(14f)
            document.add(title)
            document.add(Paragraph("\n")) // Add spacing
            document.add(description)
            document.add(Paragraph("\n\n")) // More spacing


            if(startDate.equals("all")){
                document.add(Paragraph("Report Duration").setBold())
                document.add(Paragraph("All " ))
                document.add(Paragraph("\n"))
            }else if(startDate.equals(endDate)){
                document.add(Paragraph("Report Duration").setBold())
                document.add(Paragraph("Date : "+startDate))
                document.add(Paragraph("\n"))
            }
                else {
                // Add report duration
                document.add(Paragraph("Report Duration").setBold())
                document.add(Paragraph("From: " + startDate + "\nTo: " + endDate))
                document.add(Paragraph("\n"))
            }
            // Add data summary
            document.add(Paragraph("Data Summary").setBold())
            document.add(Paragraph("Total Income: ₹"+income+"\nTotal Expense: ₹"+expense+" \nBalance: ₹"+balance))
            document.add(Paragraph("\n"))

            // Add transaction sheet as a table
            val columnWidths = floatArrayOf(3f, 3f, 3f, 3f, 3f, 3f) // Set column widths
            val table = Table(columnWidths)
            table.addCell("ID")
            table.addCell("Type")
            table.addCell("Source or Category")
            table.addCell("Amount")
            table.addCell("Date")
            table.addCell("Time")

            // Sample data for the table

            var count : Int = 0
            // Add rows to the table
            for (transaction in transactions) {
                count++
                val id = Cell().add(Paragraph(count.toString()))
                id.setPadding(5f)
                table.addCell(id)

                val type1 = Cell().add(Paragraph(transaction.type.toString()))
                type1.setPadding(5f)
                table.addCell(type1)

                val sc = Cell().add(Paragraph(transaction.sourceOrCategory.toString()))
                sc.setPadding(5f)
                table.addCell(sc)

                val am = Cell().add(Paragraph(transaction.amount.toString()))
                am.setPadding(5f)
                table.addCell(am)

                val d = Cell().add(Paragraph(transaction.date.toString()))
                d.setPadding(5f)
                table.addCell(d)

                val t = Cell().add(Paragraph(transaction.time.toString()))
                t.setPadding(5f)
                table.addCell(t)


            }
            document.add(table)
            document.add(Paragraph("\n"))

            // Add signature
            document.add(Paragraph("Generated by Xpensify at 2024-12-22 10:00 AM").setItalic())

            // Close the document
            document.close()
            println("PDF created successfully at $path/$fileName")
            Toast.makeText(requireContext(), "PDF created successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to create PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun checkStoragePermission(context: Context): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        return if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(permission),
                100
            )
            false
        } else {
            true
        }
    }




}
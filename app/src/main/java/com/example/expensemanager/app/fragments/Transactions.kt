package com.example.expensemanager.app.fragments

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.app.R
import com.example.expensemanager.app.adapters.TransactionAdapter
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.Transaction
import com.example.expensemanager.app.database.TransactionRepository
import com.example.expensemanager.app.database.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class Transactions : Fragment() {


    private lateinit var balanceTv: TextView
    private lateinit var income: TextView
    private lateinit var expense: TextView
    private lateinit var viewModel: TransactionViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var  recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var view  :  View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val v:View =   inflater.inflate(R.layout.fragment_transactions, container, false)

        expense = v.findViewById<TextView>(R.id.expense_transaction_card)
        balanceTv = v.findViewById<TextView>(R.id.balance_transaction_card)
        income = v.findViewById<TextView>(R.id.income_transaction_card)
        recyclerView = v.findViewById(R.id.recyclerView_transaction)
        initUi()
        view=v
        return v
    }

    private fun initUi() {

        var balance : Double = 0.0

        CoroutineScope(Dispatchers.IO).launch {
            val totalIncome = context?.let { AppDatabase.getDatabase(it).incomeDao().getTotalIncome() }
            CoroutineScope(Dispatchers.Main).launch {
                if (totalIncome != null) {
                   income.text = totalIncome.toString()
                   balance=balance+totalIncome.toDouble()


                }
            }

            val totalExpense = context?.let { AppDatabase.getDatabase(it).expenseDao().getTotalExpenses() }
            CoroutineScope(Dispatchers.Main).launch {
                if (totalExpense != null) {
                    expense.text = totalExpense.toString()
                    balance=balance-totalExpense.toDouble()

                    balanceTv.text = balance.toString()
                }
            }
        }



        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Setup RecyclerView

        transactionAdapter = TransactionAdapter(requireContext(),emptyList())
        recyclerView.adapter = transactionAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe data from ViewModel
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.updateData(transactions)
        }

        // Fetch transactions
        viewModel.fetchTransactions()

    }



}
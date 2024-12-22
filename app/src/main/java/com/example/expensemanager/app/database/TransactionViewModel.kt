package com.example.expensemanager.app.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application):AndroidViewModel(application) {

    private val repository: TransactionRepository

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions


    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            val data = repository.getAllTransactions()
            _transactions.postValue(data)
        }
    }

    fun fetchDayTransactions(date:String) {
        viewModelScope.launch {
            val data = repository.getDayTransactions(date)
            _transactions.postValue(data)
        }
    }

    fun fetchTransactionsInRange(startDate:String,endDate: String) {
        viewModelScope.launch {
            val data = repository.getTransactionsInRange(startDate, endDate)
            _transactions.postValue(data)
        }
    }
}
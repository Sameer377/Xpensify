package com.example.expensemanager.app.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryData>>()
    val categories: LiveData<List<CategoryData>> = _categories

    private val _totalExpense = MutableLiveData<Double>()
    val totalExpense: LiveData<Double> = _totalExpense

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = appDatabase.transactionDao().getCategoryData()
            val totalExpense = appDatabase.transactionDao().getTotalExpense()
            val totalIncome = appDatabase.transactionDao().getTotalIncome()
            withContext(Dispatchers.Main) {
                _categories.value = categories
                _totalExpense.value = totalExpense
                _totalIncome.value = totalIncome
            }
        }
    }

    fun loadTransactionsForDate(startDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = appDatabase.transactionDao().getCategoryForTheDate(startDate)
            val totalExpense = appDatabase.transactionDao().getTotalExpenseOfTheDate(startDate)
            val totalIncome = appDatabase.transactionDao().getTotalIncomeOfTheDate(startDate)
            withContext(Dispatchers.Main) {
                _totalExpense.value = totalExpense
                _totalIncome.value = totalIncome
                _categories.value = categories

            }
        }
    }

    fun loadTransactionsForDateRange(startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = appDatabase.transactionDao().getCategoryDataInRange(startDate,endDate)
            val totalExpense = appDatabase.transactionDao().getTotalExpenseInRange(startDate, endDate)
            val totalIncome = appDatabase.transactionDao().getTotalIncomeInRange(startDate, endDate)
            withContext(Dispatchers.Main) {
                _totalExpense.value = totalExpense
                _totalIncome.value = totalIncome
                _categories.value = categories

            }
        }
    }
}

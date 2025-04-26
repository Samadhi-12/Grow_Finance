package com.app.growfinance.transactions

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.growfinance.R
import com.app.growfinance.model.Transaction
import com.app.growfinance.data.PreferenceManager
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val preferenceManager: PreferenceManager,
    private val context: Context
) : ViewModel() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    init {
        loadTransactions()
    }

    // Load transactions from PreferenceManager
    fun loadTransactions() {
        viewModelScope.launch {
            try {
                val allTransactions = preferenceManager.getTransactions()
                _transactions.value = allTransactions.sortedByDescending { it.date }
            } catch (e: Exception) {
                e.printStackTrace() // Log or show an error message to the user
                _transactions.value = emptyList()
            }
        }
    }

    // Add new transaction and reload transactions
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                preferenceManager.addTransaction(transaction)
                loadTransactions()
            } catch (e: Exception) {
                e.printStackTrace() // Log or show an error message to the user
            }
        }
    }

    // Update an existing transaction and reload transactions
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                preferenceManager.updateTransaction(transaction)
                loadTransactions()
            } catch (e: Exception) {
                e.printStackTrace() // Log or show an error message to the user
            }
        }
    }

    // Delete a transaction and reload transactions
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                // Make sure to pass the unique identifier (e.g., transaction.id) to delete the correct transaction
                preferenceManager.deleteTransaction(transaction.id)
                loadTransactions()
            } catch (e: Exception) {
                e.printStackTrace() // Log or show an error message to the user
            }
        }
    }

    // Fetch available transaction categories from resources
    fun getCategories(): List<String> {
        return try {
            context.resources.getStringArray(R.array.transaction_categories).toList()
        } catch (e: Exception) {
            emptyList() // Return empty list if fetching categories fails
        }
    }
}

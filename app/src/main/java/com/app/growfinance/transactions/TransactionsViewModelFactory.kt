package com.app.growfinance.transactions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.growfinance.data.PreferenceManager
import com.app.growfinance.transactions.TransactionsViewModel

class TransactionsViewModelFactory(
    private val preferenceManager: PreferenceManager,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the ViewModel class is TransactionsViewModel
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            // Safely create and return the TransactionsViewModel
            @Suppress("UNCHECKED_CAST")
            return TransactionsViewModel(preferenceManager, context) as T
        }
        // Throw an exception if the ViewModel class is unknown
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

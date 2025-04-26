package com.app.growfinance.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.growfinance.data.PreferenceManager
import com.app.growfinance.data.Result
import com.app.growfinance.model.Transaction

class EditTransactionViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    fun getCategories(): List<String> {
        return preferenceManager.getCategories()
    }

    fun updateTransaction(transaction: Transaction) {
        try {
            preferenceManager.updateTransaction(transaction)
            _updateResult.value = Result.Success(Unit)
        } catch (e: Exception) {
            _updateResult.value = Result.Error(e)
        }
    }
}

class EditTransactionViewModelFactory(
    private val preferenceManager: PreferenceManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditTransactionViewModel(preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
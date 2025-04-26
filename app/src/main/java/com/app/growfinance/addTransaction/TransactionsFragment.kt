package com.app.growfinance.addTransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.app.growfinance.data.PreferenceManager
import com.app.growfinance.R
import com.app.growfinance.model.Transaction
import com.app.growfinance.model.TransactionType
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.RadioGroup
import com.app.growfinance.NotificationHelper

class TransactionsFragment : Fragment() {

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var titleEditText: TextInputEditText
    private lateinit var amountEditText: TextInputEditText
    private lateinit var radioGroupType: RadioGroup
    private lateinit var spinnerCategory: Spinner
    private lateinit var buttonSave: MaterialButton
    private lateinit var buttonCancel: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        preferenceManager = PreferenceManager(requireContext())

        titleEditText = view.findViewById(R.id.editTextTitle)
        amountEditText = view.findViewById(R.id.editTextAmount)
        radioGroupType = view.findViewById(R.id.radioGroupType)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonCancel = view.findViewById(R.id.buttonCancel)

        val categories = arrayOf(
            Transaction.FOOD,
            Transaction.TRANSPORT,
            Transaction.BILLS,
            Transaction.ENTERTAINMENT,
            Transaction.OTHER
        )

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        buttonSave.setOnClickListener {
            val title = titleEditText.text.toString()
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val type = if (radioGroupType.checkedRadioButtonId == R.id.radioIncome) {
                TransactionType.INCOME
            } else {
                TransactionType.EXPENSE
            }
            val category = spinnerCategory.selectedItem.toString()

            if (title.isEmpty() || amount == null || amount <= 0) {
                amountEditText.error = "Please enter a valid amount"
                return@setOnClickListener
            }

            val transaction = Transaction(
                title = title,
                amount = amount,
                category = category,
                date = java.util.Date(),
                type = type
            )


            val currentTransactions = preferenceManager.getTransactions().toMutableList()
            currentTransactions.add(transaction)
            preferenceManager.saveTransactions(currentTransactions)
            NotificationHelper.pushTransactionNotification(transaction.title, amount.toFloat(), transaction.category, transaction.date.toString() , transaction.type.toString(),requireContext())


            if (type == TransactionType.INCOME){
                preferenceManager.saveMonthlyBudget(amount)
            }

            titleEditText.text?.clear()
            amountEditText.text?.clear()


        }

        buttonCancel.setOnClickListener {
            titleEditText.text?.clear()
            amountEditText.text?.clear()
            radioGroupType.clearCheck()
            spinnerCategory.setSelection(0)
        }

        return view
    }
}

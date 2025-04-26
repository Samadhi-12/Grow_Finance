package com.app.growfinance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.growfinance.data.PreferenceManager
import com.app.growfinance.model.Transaction
import com.app.growfinance.model.TransactionType
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.textfield.TextInputEditText

class AnalysisFragment : Fragment() {

    private lateinit var budgetInput: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var budgetStatusTextView: TextView
    private lateinit var barChart: BarChart
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis, container, false)

        budgetInput = view.findViewById(R.id.setMonthlyBudget)
        saveButton = view.findViewById(R.id.btnSaveBudget)
        progressBar = view.findViewById(R.id.progressBudget)
        budgetStatusTextView = view.findViewById(R.id.tvBudgetStatus)
        barChart = view.findViewById(R.id.barChart)

        preferenceManager = PreferenceManager(requireContext())

        val savedBudget = preferenceManager.getMonthlyBudget()
        if (savedBudget > 0) {
            budgetInput.setText(savedBudget.toString())
            updateProgress(savedBudget)
            updateBarChart(savedBudget)
        }

        saveButton.setOnClickListener {
            val budgetText = budgetInput.text.toString()
            if (budgetText.isNotEmpty()) {
                try {
                    val budget = budgetText.toDouble()
                    preferenceManager.saveMonthlyBudget(budget)

                    val transaction = Transaction(
                        title = "Budget",
                        amount = budget,
                        category = "Income",
                        date = java.util.Date(),
                        type = TransactionType.INCOME
                    )

                    val currentTransactions = preferenceManager.getTransactions().toMutableList()
                    currentTransactions.add(transaction)
                    preferenceManager.saveTransactions(currentTransactions)

                    Toast.makeText(requireContext(), "Monthly budget saved!", Toast.LENGTH_SHORT).show()
                    updateProgress(budget)
                    updateBarChart(budget)
                    NotificationHelper.pushTransactionNotification(transaction.title, budget.toFloat(), transaction.category, transaction.date.toString() , transaction.type.toString(),requireContext())
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a budget", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun updateProgress(monthlyBudget: Double) {
        val currentExpenses = preferenceManager.getMonthlyExpenses()

        val progress = if (monthlyBudget > 0) {
            (currentExpenses / monthlyBudget * 100).toInt()
        } else {
            0
        }

        progressBar.progress = progress
        progressBar.indeterminateTintList = ContextCompat.getColorStateList(requireContext(), R.color.black)

        budgetStatusTextView.text = "Progress: $currentExpenses / $monthlyBudget ($progress%)"
    }

    private fun updateBarChart(monthlyBudget: Double) {
        val currentExpenses = preferenceManager.getMonthlyExpenses()

        val entries = ArrayList<com.github.mikephil.charting.data.BarEntry>()
        entries.add(com.github.mikephil.charting.data.BarEntry(0f, currentExpenses.toFloat())) // Expenses
        entries.add(com.github.mikephil.charting.data.BarEntry(1f, monthlyBudget.toFloat())) // Budget

        val barDataSet = BarDataSet(entries, "Budget vs Expenses")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList() // Set different colors for the bars

        val barData = BarData(barDataSet)

        barChart.data = barData

        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }
}

package com.app.growfinance.wallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.growfinance.R
import com.app.growfinance.data.PreferenceManager
import com.app.growfinance.model.Transaction
import com.app.growfinance.model.TransactionType
import com.app.growfinance.databinding.FragmentWalletBinding
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class WalletFragment : Fragment() {
    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WalletViewModel
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentWalletBinding.inflate(inflater, container, false)
            return binding.root
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error in onCreateView: ${e.message}")
            throw e
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {

            setupViewModel()
            setupUI()
            observeViewModel()
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error in onViewCreated: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun setupViewModel() {
        val preferenceManager = PreferenceManager(requireContext())
        val factory = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WalletViewModel(preferenceManager) as T
            }
        }
        viewModel = ViewModelProvider(this, factory).get(WalletViewModel::class.java)
    }

    private fun setupUI() {
        setupPieChart()
        setupLineCharts()

        // Add this for navigation
        binding.AddBudget.setOnClickListener {
            findNavController().navigate(R.id.action_walletFragment_to_addTransactionFragment)
        }
    }


    private fun observeViewModel() {
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        try {
            viewModel.loadDashboardData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        val preferenceManager = PreferenceManager(requireContext())

        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            try {
                binding.tvTotalBalance.text = "${preferenceManager.getCurrency()} ${formatCurrency(balance ?: 0.0)}"
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvTotalBalance.text = "${preferenceManager.getCurrency()} ${formatCurrency(0.0)}"
            }
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            try {
                binding.tvTotalIncome.text =  "${preferenceManager.getCurrency()} ${formatCurrency(income ?: 0.0)}"
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvTotalIncome.text = "${preferenceManager.getCurrency()} ${formatCurrency(0.0)}"
            }
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            try {
                binding.tvTotalExpense.text = "${preferenceManager.getCurrency()} ${formatCurrency(expense ?: 0.0)}"
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvTotalExpense.text = "${preferenceManager.getCurrency()} ${formatCurrency(0.0)}"
            }
        }

        viewModel.categorySpending.observe(viewLifecycleOwner) { spending ->
            try {
                updatePieChart(spending ?: emptyMap())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            try {
                updateLineCharts(transactions ?: emptyList())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupPieChart() {
        try {
            binding.pieChart.apply {
                description.isEnabled = false
                legend.isEnabled = true
                setHoleColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                setTransparentCircleColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                setEntryLabelColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                setEntryLabelTextSize(12f)
                setUsePercentValues(true)
                setDrawEntryLabels(true)
                setDrawHoleEnabled(true)
                setHoleRadius(50f)
                setTransparentCircleRadius(55f)
                setRotationEnabled(true)
                setHighlightPerTapEnabled(true)
                animateY(1000)
                setNoDataText("No transactions yet")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupLineCharts() {
        try {
            val commonSetup: com.github.mikephil.charting.charts.LineChart.() -> Unit = {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return try {
                            dateFormat.format(Date(value.toLong()))
                        } catch (e: Exception) {
                            ""
                        }
                    }
                }
                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return formatCurrency(value.toDouble())
                    }
                }
                axisRight.isEnabled = false
                setNoDataText("No data available")
            }

            binding.incomeChart.apply(commonSetup)
            binding.expenseChart.apply(commonSetup)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updatePieChart(spending: Map<String, Double>) {
        try {
            if (spending.isEmpty()) {
                binding.pieChart.setNoDataText("No transactions yet")
                binding.pieChart.invalidate()
                return
            }

            val entries = spending.map { (category, amount) ->
                PieEntry(amount.toFloat(), category)
            }

            val dataSet = PieDataSet(entries, "Categories").apply {
                colors = entries.map { entry ->
                    if (entry.label.startsWith("Income:")) {
                        ContextCompat.getColor(requireContext(), R.color.green_500)
                    } else {
                        ContextCompat.getColor(requireContext(), R.color.red_500)
                    }
                }
                valueFormatter = PercentFormatter(binding.pieChart)
                valueTextSize = 12f
                valueTextColor = ContextCompat.getColor(requireContext(), android.R.color.black)
            }

            binding.pieChart.data = PieData(dataSet)
            binding.pieChart.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
            binding.pieChart.setNoDataText("Error loading data")
            binding.pieChart.invalidate()
        }
    }

    private fun updateLineCharts(transactions: List<Transaction>) {
        try {
            val incomeEntries = transactions
                .filter { it.type == TransactionType.INCOME }
                .groupBy { it.date }
                .map { (date, transactions) ->
                    Entry(date.time.toFloat(), transactions.sumOf { it.amount }.toFloat())
                }
                .sortedBy { it.x }

            val expenseEntries = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.date }
                .map { (date, transactions) ->
                    Entry(date.time.toFloat(), transactions.sumOf { it.amount }.toFloat())
                }
                .sortedBy { it.x }

            // Update Income Chart
            if (incomeEntries.isNotEmpty()) {
                val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.green_500)
                    setDrawCircles(true)
                    setDrawValues(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return formatCurrency(value.toDouble())
                        }
                    }
                }
                binding.incomeChart.data = LineData(incomeDataSet)
            } else {
                binding.incomeChart.data = null
                binding.incomeChart.setNoDataText("No income transactions yet")
            }
            binding.incomeChart.invalidate()

            // Update Expense Chart
            if (expenseEntries.isNotEmpty()) {
                val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.red_500)
                    setDrawCircles(true)
                    setDrawValues(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return formatCurrency(value.toDouble())
                        }
                    }
                }
                binding.expenseChart.data = LineData(expenseDataSet)
            } else {
                binding.expenseChart.data = null
                binding.expenseChart.setNoDataText("No expense transactions yet")
            }
            binding.expenseChart.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
            binding.incomeChart.setNoDataText("Error loading data")
            binding.expenseChart.setNoDataText("Error loading data")
            binding.incomeChart.invalidate()
            binding.expenseChart.invalidate()
        }
    }

    private fun formatCurrency(amount: Double): String {
        return try {
            val format = NumberFormat.getNumberInstance(Locale.getDefault())
            format.minimumFractionDigits = 2
            format.maximumFractionDigits = 2
            format.format(amount)
        } catch (e: Exception) {
            e.printStackTrace()
            "0.00"
        }
    }
} 
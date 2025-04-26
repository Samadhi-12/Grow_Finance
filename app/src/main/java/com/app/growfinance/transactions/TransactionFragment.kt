package com.app.growfinance.transactions

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.growfinance.R
import com.app.growfinance.model.Transaction
import com.app.growfinance.model.TransactionType
import com.app.growfinance.databinding.FragmentTransactionBinding
import com.app.growfinance.data.PreferenceManager
import com.app.growfinance.transactions.TransactionsAdapter
import com.app.growfinance.transactions.TransactionsViewModel
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private val viewModel: TransactionsViewModel by viewModels {
        TransactionsViewModelFactory(PreferenceManager(requireContext()), requireContext())
    }
    private lateinit var adapter: TransactionsAdapter
    private var allTransactions: List<Transaction> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
//        setupClickListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Ensure the ViewModel loads transactions only if the fragment is added
        if (isAdded && !isDetached) {
            viewModel.loadTransactions()
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionsAdapter(
            onEditClick = { transaction ->
                val action = TransactionFragmentDirections
                    .actionNavigationTransactionsToEditTransactionFragment(transaction)
                findNavController().navigate(action)
            },
            onDeleteClick = { transaction ->
                showDeleteConfirmationDialog(transaction)
            }
        )
        binding.recyclerViewTransactions.adapter = adapter
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(requireContext())
    }

//    private fun setupClickListeners() {
//        binding.fabAddTransaction.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_transactions_to_addTransactionFragment)
//        }
//    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            allTransactions = transactions
            adapter.submitList(allTransactions)
        }

        binding.btnAll.setOnClickListener {
            adapter.submitList(allTransactions)
        }

        binding.btnIncome.setOnClickListener {
            val incomeList = allTransactions.filter { it.type == TransactionType.INCOME }
            adapter.submitList(incomeList)
        }

        binding.btnExpense.setOnClickListener {
            val expenseList = allTransactions.filter { it.type == TransactionType.EXPENSE }
            adapter.submitList(expenseList)
        }
    }


    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTransaction(transaction)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cleanup any resources if needed
    }
}

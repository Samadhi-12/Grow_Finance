package com.app.growfinance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import android.widget.Button
import android.widget.Spinner
import com.app.growfinance.data.PreferenceManager

class SettingsFragment : Fragment() {

    private lateinit var spinnerCurrency: Spinner
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var btnSaveCurrency: Button
    private lateinit var btnBackup: Button
    private lateinit var btnRestore: Button
    private lateinit var resetBtn: Button
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        preferenceManager = PreferenceManager(requireContext())

        spinnerCurrency = view.findViewById(R.id.spinnerCurrency)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        btnSaveCurrency = view.findViewById(R.id.btnSaveCurrency)
        btnBackup = view.findViewById(R.id.btnBackup)
        btnRestore = view.findViewById(R.id.btnRestore)
        resetBtn = view.findViewById(R.id.resetbtn)

        val currencies = arrayOf("USD", "EUR", "LKR", "GBP", "AUD")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter

        val savedCurrency = preferenceManager.getCurrency()
        if (savedCurrency != null) {
            val position = currencies.indexOf(savedCurrency)
            if (position >= 0) {
                spinnerCurrency.setSelection(position)
            }
        }

        btnSaveCurrency.setOnClickListener {
            val selectedCurrency = spinnerCurrency.selectedItem.toString()
            if (selectedCurrency.isNotEmpty()) {
                preferenceManager.saveCurrency(selectedCurrency)
                Toast.makeText(requireContext(), "Currency saved: $selectedCurrency", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please select a currency", Toast.LENGTH_SHORT).show()
            }
        }



        btnBackup.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, "grow_finance_backup.json")
            }
            startActivityForResult(intent, REQUEST_CODE_BACKUP)
        }

        btnRestore.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
            }
            startActivityForResult(intent, REQUEST_CODE_RESTORE)
        }

        resetBtn.setOnClickListener(){
            context?.let { it1 -> preferenceManager.clearAllData(it1) }
            NotificationHelper.dataResetNotification(requireContext())
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data

            if (uri != null) {
                when (requestCode) {
                    REQUEST_CODE_BACKUP -> {
                        preferenceManager.exportSharedPreferencesToUri(requireContext(), uri)
                    }

                    REQUEST_CODE_RESTORE -> {
                        preferenceManager.importSharedPreferencesFromUri(requireContext(), uri)
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_BACKUP = 1
        private const val REQUEST_CODE_RESTORE = 2
    }
}

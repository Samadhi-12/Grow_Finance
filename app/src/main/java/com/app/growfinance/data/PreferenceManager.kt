package com.app.growfinance.data

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.app.growfinance.R
import com.app.growfinance.model.Transaction
import com.app.growfinance.model.TransactionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val context = context

    companion object {
        private const val PREF_NAME = "GrowFinancePrefs"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_TRANSACTIONS = "transactions"
    }

    fun saveMonthlyBudget(budget: Double) {
        sharedPreferences.edit().putFloat(KEY_MONTHLY_BUDGET, budget.toFloat()).apply()
    }

    fun getMonthlyBudget(): Double {
        return sharedPreferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
    }

    fun saveCurrency(currency: String) {
        sharedPreferences.edit().putString(KEY_CURRENCY, currency).apply()
    }

    fun getCurrency(): String? {
        return sharedPreferences.getString(KEY_CURRENCY, "LKR")
    }

    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
    }


    fun getTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(KEY_TRANSACTIONS, "[]")
        val type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }


    fun addTransaction(transaction: Transaction) {
        val currentTransactions = getTransactions().toMutableList()
        currentTransactions.add(transaction)
        saveTransactions(currentTransactions)
    }

    fun deleteTransaction(transactionId: String) {
        val currentTransactions = getTransactions().toMutableList()
        currentTransactions.removeIf { it.id == transactionId }
        saveTransactions(currentTransactions)
    }

    fun updateTransaction(updatedTransaction: Transaction) {
        val currentTransactions = getTransactions().toMutableList()
        val index = currentTransactions.indexOfFirst { it.id == updatedTransaction.id }
        if (index != -1) {
            currentTransactions[index] = updatedTransaction
            saveTransactions(currentTransactions)
        }
    }

    fun getMonthlyExpenses(): Double {
        val currentMonth = Date().month
        return getTransactions()
            .filter { it.type == TransactionType.EXPENSE && it.date.month == currentMonth }
            .sumOf { it.amount }
    }

    // Backup and Restore methods
    fun getBackupFiles(): List<File> {
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        return backupDir.listFiles { file ->
            file.name.startsWith("GrowFinance_Backup_") && file.name.endsWith(".json")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    fun createBackup(backupData: String): Boolean {
        try {
            val dateFormat =
                java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
            val fileName = "GrowFinance_Backup_${dateFormat.format(Date())}.json"
            val backupDir = File(context.filesDir, "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            val backupFile = File(backupDir, fileName)
            FileWriter(backupFile).use { writer ->
                writer.write(backupData)
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun restoreFromBackup(backupFile: File): Boolean {
        try {
            val backupData = backupFile.readText()
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val data = gson.fromJson<Map<String, Any>>(backupData, type)

            data["transactions"]?.let {
                val transactions = gson.fromJson<List<Transaction>>(
                    gson.toJson(it),
                    object : TypeToken<List<Transaction>>() {}.type
                )
                saveTransactions(transactions)
            }

            data["budget"]?.let {
                saveMonthlyBudget((it as Number).toDouble())
            }

            data["currency"]?.let {
                saveCurrency(it as String)
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun clearAllData(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun exportSharedPreferencesToUri(context: Context, uri: Uri) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        val jsonObject = JSONObject()
        for ((key, value) in allEntries) {
            jsonObject.put(key, value)
        }

        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonObject.toString(4).toByteArray())
                outputStream.flush()
            }
            Toast.makeText(context, "Backup saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun importSharedPreferencesFromUri(context: Context, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val json = inputStream?.bufferedReader().use { it?.readText() }

            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val gson = Gson()
            val prefsMap: Map<*, *>? = gson.fromJson(json, Map::class.java)

            if (prefsMap != null) {
                prefsMap.forEach { (key, value) ->
                    when (value) {
                        is String -> editor.putString(key.toString(), value)
                        is Float -> editor.putFloat(key.toString(), value)
                        is Int -> editor.putInt(key.toString(), value)
                        is Boolean -> editor.putBoolean(key.toString(), value)
                        is Long -> editor.putLong(key.toString(), value)
                        is Double -> editor.putFloat(key.toString(), value.toFloat()) // Handle double from Gson
                        else -> Log.w("ImportPrefs", "Unsupported type: $key = $value")
                    }
                }
            }

            editor.apply()
            Toast.makeText(context, "Data imported successfully", Toast.LENGTH_SHORT).show()

            // Optional: suggest app restart
            AlertDialog.Builder(context)
                .setTitle("Import Complete")
                .setMessage("Restart app to apply the changes.")
                .setPositiveButton("Restart") { _, _ ->
                    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }
                .setNegativeButton("Later", null)
                .show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCategories(): List<String> {
        return try {
            context.resources.getStringArray(R.array.transaction_categories).toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

package com.app.growfinance.wallet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.growfinance.R
import com.app.growfinance.databinding.ActivityWalletBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class WalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityWalletBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setupNavigation()
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in onCreate: ${e.message}")
            e.printStackTrace()
        }

    }

    private fun setupNavigation() {
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView?.setupWithNavController(navController)
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in setupNavigation: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
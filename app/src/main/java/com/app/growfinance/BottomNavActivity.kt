package com.app.growfinance

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.app.growfinance.addTransaction.TransactionsFragment
import com.app.growfinance.wallet.WalletFragment
import com.app.growfinance.databinding.ActivityBottomNavBinding
import com.app.growfinance.transactions.TransactionFragment
import com.google.android.material.navigation.NavigationView


class BottomNavActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout , binding.toolbar, R.string.nav_open, R.string.nav_close)
//        binding.drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigationView.background =null

        binding.bottomNavigationView.setOnItemSelectedListener{
                item->when(item.itemId) {
            R.id.wallet -> openFragment(WalletFragment())
            R.id.analytics -> openFragment(AnalysisFragment())
            R.id.transactions -> openFragment(TransactionFragment())
            R.id.Settings -> openFragment(SettingsFragment())
        }
            true
        }
        fragmentManager= supportFragmentManager
        openFragment(WalletFragment())

        binding.addfab.setOnClickListener {
            openFragment(TransactionsFragment())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.wallet -> openFragment(WalletFragment())
            R.id.analytics -> openFragment(AnalysisFragment())
            R.id.transactions -> openFragment(TransactionFragment())
            R.id.Settings -> openFragment(SettingsFragment())

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (!onBackPressedDispatcher.hasEnabledCallbacks()) {
                super.onBackPressed()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }

        }
    }

    private fun openFragment(fragment: Fragment){
        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container ,fragment)
        fragmentTransaction.commit()
    }


}
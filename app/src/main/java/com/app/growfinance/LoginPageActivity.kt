package com.app.growfinance

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.growfinance.wallet.WalletFragment
import com.app.growfinance.wallet.WalletActivity
import com.app.growfinance.databinding.ActivityLoginPageBinding

class LoginPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private val correctPasscode = "1234"
    private val correctName = "samadhi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()

    }


    private fun setupClickListeners() {

        binding.loginredirectText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

        binding.loginButton.setOnClickListener {
            val enteredName = binding.loginName.text.toString()
            val enteredPasscode = binding.loginPassword.text.toString()

            if (enteredName == correctName) {
                if (enteredPasscode == correctPasscode) {
                    val intent = Intent(this, BottomNavActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                    binding.loginPassword.text?.clear()
                }
            } else {
                Toast.makeText(this, "Incorrect name. Please try again.", Toast.LENGTH_SHORT).show()
                binding.loginName.text?.clear()
            }

        }
    }
}
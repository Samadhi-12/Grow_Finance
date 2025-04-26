package com.app.growfinance

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.growfinance.databinding.ActivityLoginPageBinding
import com.app.growfinance.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()

    }
    private fun setupClickListeners() {
        binding.signupredirectText.setOnClickListener {
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)

        }

    }
    }

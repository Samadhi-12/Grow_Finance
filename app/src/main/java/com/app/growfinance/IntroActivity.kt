package com.app.growfinance

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.growfinance.databinding.ActivityIntroBinding
import com.app.growfinance.databinding.ActivityRegisterBinding


class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()

    }

    private fun setupClickListeners() {
        binding.introButton.setOnClickListener {
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)

        }

    }
}

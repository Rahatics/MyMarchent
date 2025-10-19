package com.mymarchent.mymarchent.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mymarchent.mymarchent.MainActivity
import com.mymarchent.mymarchent.data.local.SessionManager
import com.mymarchent.mymarchent.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        // Check if the user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return // Finish LoginActivity immediately
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnLinkAccount.isEnabled = !isLoading
        }

        viewModel.authResult.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Account Linked Successfully!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        binding.btnLinkAccount.setOnClickListener {
            val apiKey = binding.etApiKey.text.toString().trim()
            val secretKey = binding.etSecretKey.text.toString().trim()

            if (apiKey.isNotEmpty() && secretKey.isNotEmpty()) {
                viewModel.verifyApiKeys(apiKey, secretKey)
            } else {
                Toast.makeText(this, "Please enter both API and Secret Key", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Prevents user from coming back to LoginActivity with the back button
    }
}

package com.symplified.ordertaker.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.databinding.ActivityLoginBinding
import com.symplified.ordertaker.ui.main.MainActivity
import com.symplified.ordertaker.viewmodels.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            authViewModel.logout()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {

        authViewModel.logout()

        binding.tvEmail.editText!!.doOnTextChanged { text, _, _, _ -> authViewModel.setUsername(text!!.toString()) }
        binding.tvPassword.editText!!.doOnTextChanged { text, _, _, _ -> authViewModel.setPassword(text!!.toString()) }

        binding.btnLogin.setOnClickListener {
            authViewModel.tryLogin()
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loginProgressLayout.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
            } else {
                binding.loginProgressLayout.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }

        authViewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (isAuthenticated) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        authViewModel.errorMessage.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        authViewModel.usernameError.observe(this) { errorMessage ->
            binding.tvEmail.error = errorMessage
        }
        authViewModel.passwordError.observe(this) { errorMessage ->
            binding.tvPassword.error = errorMessage
        }
    }
}
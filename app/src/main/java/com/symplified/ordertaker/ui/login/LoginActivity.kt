package com.symplified.ordertaker.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.ActivityLoginBinding
import com.symplified.ordertaker.databinding.ActivityMainBinding
import com.symplified.ordertaker.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.btnLogin.setOnClickListener {
            viewModel.tryLogin(this)
        }

        val isLoadingObserver = Observer<Boolean> { isLoading ->
            if (isLoading) {
                binding.loginProgressLayout.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
            } else {
                binding.loginProgressLayout.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }
        viewModel.isLoading.observe(this, isLoadingObserver)
        viewModel.isAuthenticated.observe(this) { isAuthenticated ->
            if (isAuthenticated) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
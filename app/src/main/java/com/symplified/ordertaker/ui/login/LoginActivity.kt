package com.symplified.ordertaker.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.symplified.ordertaker.BuildConfig
import com.symplified.ordertaker.R
import com.symplified.ordertaker.databinding.ActivityLoginBinding
import com.symplified.ordertaker.ui.main.MainActivity
import com.symplified.ordertaker.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import java.util.*

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

//        binding.tvEmail.editText!!.doOnTextChanged { text, _, _, _ -> authViewModel.setUsername(text!!.toString()) }
//        binding.tvPassword.editText!!.doOnTextChanged { text, _, _, _ ->
//            authViewModel.setPassword(
//                text!!.toString()
//            )
//        }

        binding.tvEmail.editText!!.doAfterTextChanged { authViewModel.setUsername(it.toString()) }
        binding.tvPassword.editText!!.doAfterTextChanged { authViewModel.setPassword(it.toString()) }

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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.isStaging.collect { isStaging ->
                    binding.tvEmail.editText!!.text.clear()
                    binding.tvEmail.error = null
                    binding.tvPassword.editText!!.text.clear()
                    binding.tvPassword.error = null
//                    binding.tvEmail.requestFocus()

                    binding.btnSwitchToProduction.visibility =
                        if (isStaging) View.VISIBLE else View.GONE
                    binding.welcome.text =
                        if (isStaging) getString(R.string.staging_mode) else getString(R.string.welcome_message)

                }
            }
        }

        binding.appVersionText!!.text = getString(
            R.string.version_indicator,
            Calendar.getInstance().get(Calendar.YEAR),
            BuildConfig.VERSION_NAME
        )

        binding.btnSwitchToProduction.setOnClickListener {
            authViewModel.switchToProduction()
        }
    }
}
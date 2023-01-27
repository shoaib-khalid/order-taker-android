package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.symplified.ordertaker.App
import com.symplified.ordertaker.models.auth.AuthRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    private val _username = MutableLiveData<String>().apply { value = "" }
    private val _usernameError: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val usernameError: LiveData<String> = _usernameError
    fun setUsername(username: String) {
        _username.value = username
        _usernameError.value = if (username.isBlank()) "Username cannot be blank" else ""
    }

    private val _password = MutableLiveData<String>().apply { value = "" }
    private val _passwordError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val passwordError: LiveData<String> = _passwordError
    fun setPassword(password: String) {
        _password.value = password
        _passwordError.value = if (password.isBlank()) "Password cannot be blank" else ""
    }

    private val _isAuthenticated: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val errorMessage: LiveData<String> = _errorMessage

    fun tryLogin() {
        if (App.isConnectedToInternet()
            && _username.value!!.isNotBlank()
            && _password.value!!.isNotBlank()
        ) {
            _isLoading.value = true
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    _isLoading.value = false
                    _errorMessage.value = "An error occurred. Please try again."
                    return@addOnCompleteListener
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val isAuthenticated =
                        App.userRepository.authenticate(
                            AuthRequest(
                                _username.value!!,
                                _password.value!!,
                                task.result
                            )
                        )

                    withContext(Dispatchers.Main) {
                        _isLoading.value = false
                        _isAuthenticated.value = isAuthenticated
                        if (!isAuthenticated) {
                            _errorMessage.value = "Username or password is incorrect."
                        }
                    }
                }
            }
        } else if (!App.isConnectedToInternet()) {
            _errorMessage.value = "Not connected to internet."
        } else {
            _usernameError.value = "Username cannot be blank."
            _passwordError.value = "Password cannot be blank."
        }
    }

    fun logout() {
        _isAuthenticated.value = false
        App.sharedPreferences().edit().clear().apply()
        CoroutineScope(Dispatchers.IO).launch {
            App.userRepository.logout()
            App.productRepository.clear()
            App.cartSubItemRepository.clear()
            App.cartItemRepository.clear()
            App.tableRepository.clear()
            App.zoneRepository.clear()
            App.paymentChannelRepository.clear()
        }
    }
}
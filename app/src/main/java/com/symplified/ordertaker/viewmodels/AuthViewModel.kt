package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.symplified.ordertaker.App
import com.symplified.ordertaker.models.auth.AuthRequestBody
import com.symplified.ordertaker.models.auth.AuthResponseBody
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private val _username = MutableLiveData<String>().apply { value = "" }
    val username: LiveData<String> = _username
    private val _usernameError: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val usernameError: LiveData<String> = _usernameError
    fun setUsername(username: String) {
        _username.value = username
        _usernameError.value = if (username.isBlank()) "Username cannot be blank" else ""
    }

    private val _password = MutableLiveData<String>().apply { value = "" }
    val password: LiveData<String> = _password
    private val _passwordError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val passwordError: LiveData<String> = _passwordError
    fun setPassword(password: String) {
        _password.value = password
        _passwordError.value = if (password.isBlank()) "Password cannot be blank" else ""
    }

    private val _isLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val errorMessage: LiveData<String> = _errorMessage

    private val _isAuthenticated = MutableLiveData<Boolean>().apply {
        val isAuthenticated = App.sharedPreferences()
            .getBoolean(App.IS_LOGGED_IN, false)
        value = isAuthenticated
    }
    val isAuthenticated: LiveData<Boolean> = _isAuthenticated

    fun tryLogin() {
        if (_username.value!!.isNotBlank() && _password.value!!.isNotBlank()) {
            viewModelScope.launch {
                _isLoading.value = true
                ServiceGenerator.createAuthService()
                    .authenticate(AuthRequestBody(_username.value!!, _password.value!!))
                    .enqueue(object : Callback<AuthResponseBody> {
                        override fun onResponse(
                            call: Call<AuthResponseBody>,
                            response: Response<AuthResponseBody>
                        ) {
                            Log.d("login-activity", response.toString())
                            if (response.isSuccessful) {
                                App.sharedPreferences()
                                    .edit()
                                    .putBoolean(App.IS_LOGGED_IN, true)
                                    .apply()
                                _isAuthenticated.value = true
                            } else {
                                _errorMessage.value =
                                    if (response.code() == 401) "Username or password is incorrect."
                                    else "An error occurred. Please try again."
                                _isLoading.value = false
                            }
                        }

                        override fun onFailure(call: Call<AuthResponseBody>, t: Throwable) {
                            _isLoading.value = false
                        }

                    })
            }
        } else {
            _usernameError.value = "Username cannot be blank."
            _passwordError.value = "Password cannot be blank."
        }
    }

    fun logout() {
        App.sharedPreferences().edit().clear().apply()
        _isAuthenticated.value = false
    }
}
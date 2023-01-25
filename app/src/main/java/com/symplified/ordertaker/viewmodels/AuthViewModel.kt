package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.models.auth.AuthRequestBody
import com.symplified.ordertaker.models.auth.AuthResponseBody
import com.symplified.ordertaker.models.auth.AuthSessionData
import com.symplified.ordertaker.models.users.UserResponseBody
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val errorMessage: LiveData<String> = _errorMessage

    private val _isAuthenticated = MutableLiveData<Boolean>().apply {
        val isAuthenticated = App.sharedPreferences()
            .getBoolean(SharedPrefsKey.IS_AUTHENTICATED, false)
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
                            if (response.isSuccessful && response.body() != null) {
                                getStoreIdAndLogin(response.body()!!.data.session)
                            } else {
                                _errorMessage.value =
                                    if (response.code() == 401) "Username or password is incorrect."
                                    else "An error occurred. Please try again."
                                _isLoading.value = false
                            }
                        }

                        override fun onFailure(call: Call<AuthResponseBody>, t: Throwable) {
                            _isLoading.value = false
                            _errorMessage.value = "An error occurred. Please try again."
                        }

                    })
            }
        } else {
            _usernameError.value = "Username cannot be blank."
            _passwordError.value = "Password cannot be blank."
        }
    }

    fun getStoreIdAndLogin(sessionData: AuthSessionData) {
        ServiceGenerator.createAuthService()
            .getUserById(sessionData.ownerId)
            .clone()
            .enqueue(object : Callback<UserResponseBody> {
                override fun onResponse(
                    call: Call<UserResponseBody>,
                    response: Response<UserResponseBody>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val userData = response.body()!!.data
                        App.sharedPreferences()
                            .edit()
                            .putString(SharedPrefsKey.USER_ID, userData.id)
                            .putString(SharedPrefsKey.USERNAME, userData.username)
                            .putString(SharedPrefsKey.STORE_ID, userData.storeId)
                            .putString(SharedPrefsKey.ACCESS_TOKEN, sessionData.accessToken)
                            .putString(SharedPrefsKey.REFRESH_TOKEN, sessionData.refreshToken)
                            .putBoolean(SharedPrefsKey.IS_AUTHENTICATED, true)
                            .apply()
                        _isAuthenticated.value = true
                    } else {
                        _isLoading.value = false
                        _errorMessage.value = "An error occurred. Please try again."
                    }
                }

                override fun onFailure(call: Call<UserResponseBody>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "An error occurred. Please try again."
                }
            })
    }

    fun logout() {
        _isAuthenticated.value = false
        App.sharedPreferences().edit().clear().apply()
        CoroutineScope(Dispatchers.IO).launch {
            App.productRepository.clear()
            App.cartSubItemRepository.clear()
            App.cartItemRepository.clear()
            App.tableRepository.clear()
            App.zoneRepository.clear()
            App.paymentChannelRepository.clear()
        }
    }
}
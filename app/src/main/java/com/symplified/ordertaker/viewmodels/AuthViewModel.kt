package com.symplified.ordertaker.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.symplified.ordertaker.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    val isLoading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val isAuthenticated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun tryLogin(context: Context) {
        viewModelScope.launch {
            isLoading.value = true
            delay(3000L)
            context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .edit()
                .putBoolean(context.getString(R.string.is_logged_in), true)
                .apply()
            isAuthenticated.value = true
        }
    }
}
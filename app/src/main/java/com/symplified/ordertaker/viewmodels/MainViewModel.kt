package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.models.users.User
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    val user: LiveData<User?> = App.userRepository.user.asLiveData()
}
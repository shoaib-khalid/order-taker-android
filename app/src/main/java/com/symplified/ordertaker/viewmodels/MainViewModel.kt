package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.symplified.ordertaker.App
import com.symplified.ordertaker.models.users.User

class MainViewModel : ViewModel() {

    val user: LiveData<User?> = App.userRepository.user.asLiveData()
}
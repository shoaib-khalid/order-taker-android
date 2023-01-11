package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.models.stores.StoreResponseBody
import com.symplified.ordertaker.models.users.UserResponseBody
import com.symplified.ordertaker.networking.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _username = MutableLiveData<String>().apply {
        Log.d("mainviewmode", "username: ${
            App.sharedPreferences().getString(SharedPrefsKey.USERNAME, "")!!
        }")
        value = App.sharedPreferences().getString(SharedPrefsKey.USERNAME, "")!!
    }
    val username: LiveData<String> = _username

    private val _storeName = MutableLiveData<String>().apply {
        value = App.sharedPreferences().getString(SharedPrefsKey.STORE_NAME, "")!!
    }
    val storeName: LiveData<String> = _storeName

    fun fetchUsername() {
        val sharedPrefs = App.sharedPreferences()
        val storedUsername = sharedPrefs.getString(SharedPrefsKey.USERNAME, "")!!
        if (storedUsername.isNotBlank()) {
            _username.value = storedUsername
        } else {
            val storedUserId = sharedPrefs.getString(SharedPrefsKey.USER_ID, "")!!
            ServiceGenerator.createAuthService()
                .getUserById(storedUserId)
                .clone()
                .enqueue(object : Callback<UserResponseBody> {
                    override fun onResponse(
                        call: Call<UserResponseBody>,
                        response: Response<UserResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val username = response.body()!!.data.username
                            _username.value = username
                            sharedPrefs.edit()
                                .putString(SharedPrefsKey.USERNAME, username)
                                .apply()
                        }
                    }

                    override fun onFailure(call: Call<UserResponseBody>, t: Throwable) {}
                })
        }
    }

    fun fetchStoreName() {
        val sharedPrefs = App.sharedPreferences()
        val storedStoreName = sharedPrefs.getString(SharedPrefsKey.STORE_NAME, "")!!
        if (storedStoreName.isNotBlank()) {
            _storeName.value = storedStoreName
        } else {
            val storedStoreId = sharedPrefs.getString(SharedPrefsKey.STORE_ID, "")!!
            ServiceGenerator.createProductService()
                .getStoreById(storedStoreId)
                .clone()
                .enqueue(object: Callback<StoreResponseBody> {
                    override fun onResponse(
                        call: Call<StoreResponseBody>,
                        response: Response<StoreResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val storeName = response.body()!!.data.name
                            _storeName.value = storeName
                            sharedPrefs
                                .edit()
                                .putString(SharedPrefsKey.STORE_NAME, storeName)
                                .apply()
                        }
                    }

                    override fun onFailure(call: Call<StoreResponseBody>, t: Throwable) {}
                })
        }
    }
}
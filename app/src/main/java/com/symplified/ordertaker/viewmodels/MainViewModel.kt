package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.symplified.ordertaker.App
import com.symplified.ordertaker.models.users.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    val user: LiveData<User?> = App.userRepository.user.asLiveData()

    private val _headerImageUrl: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val headerImageUrl: LiveData<String> = _headerImageUrl

    init {
        CoroutineScope(Dispatchers.IO).launch {
            App.userRepository.user.collect { user ->
                user?.let {
                    val storeAssets =
                        App.productRepository.getStoreAssets(user.storeId)
                    storeAssets.firstOrNull { it.assetType == "LogoUrl" }
                        ?.let { logoAsset ->
                            withContext(Dispatchers.Main) {
                                _headerImageUrl.value = logoAsset.assetUrl
                            }
                        }
                }
            }
        }
    }

    fun logout() {
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
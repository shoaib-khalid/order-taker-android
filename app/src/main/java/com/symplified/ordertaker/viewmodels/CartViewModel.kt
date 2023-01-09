package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.symplified.ordertaker.App
import com.symplified.ordertaker.data.repository.CartItemRepository
import com.symplified.ordertaker.models.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel: ViewModel() {
    val cartItems: LiveData<List<CartItem>> = App.cartItemRepository.allItems.asLiveData()

    fun insert(cartItem: CartItem) = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.insert(cartItem)
    }

    fun delete(cartItem: CartItem) = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.delete(cartItem)
    }

    fun clearAll() = CoroutineScope(Dispatchers.IO).launch {
        App.cartItemRepository.clear()
    }
}

class CartViewModelFactory(private val repository: CartItemRepository)
    : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                return CartViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
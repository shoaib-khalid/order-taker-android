package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.symplified.ordertaker.data.repository.CartItemRepository
import com.symplified.ordertaker.models.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartItemRepository): ViewModel() {
    val cartItems: LiveData<List<CartItem>> = repository.allItems.asLiveData()

    fun insert(cartItem: CartItem) = CoroutineScope(Dispatchers.IO).launch {
        repository.insert(cartItem)
    }

    fun delete(cartItem: CartItem) = CoroutineScope(Dispatchers.IO).launch {
        repository.delete(cartItem)
    }

    fun clearAll() = CoroutineScope(Dispatchers.IO).launch {
        repository.clear()
    }
}

class CartViewModelFactory(private val repository: CartItemRepository)
    : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                return CartViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
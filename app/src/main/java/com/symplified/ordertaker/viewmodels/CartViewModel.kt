package com.symplified.ordertaker.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.room.Room
import com.symplified.ordertaker.R
import com.symplified.ordertaker.data.AppDatabase
import com.symplified.ordertaker.data.repository.CartItemRepository
import com.symplified.ordertaker.models.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartItemRepository): ViewModel() {
    val cartItems: LiveData<List<CartItem>> = repository.allItems.asLiveData()

    suspend fun insert(cartItem: CartItem) = CoroutineScope(Dispatchers.IO).launch {
        repository.insert(cartItem)
    }

    suspend fun clearAll() = CoroutineScope(Dispatchers.IO).launch {
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
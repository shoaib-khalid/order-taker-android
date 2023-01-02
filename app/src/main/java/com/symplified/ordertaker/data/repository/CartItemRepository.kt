package com.symplified.ordertaker.data.repository

import androidx.annotation.WorkerThread
import com.symplified.ordertaker.data.CartItemDao
import com.symplified.ordertaker.models.CartItem
import kotlinx.coroutines.flow.Flow

class CartItemRepository(private val cartItemDao: CartItemDao) {
    val allItems: Flow<List<CartItem>> =
        cartItemDao.getAll()

    fun insert(cartItem: CartItem) {
        cartItemDao.insert(cartItem)
    }

    fun clear() {
        cartItemDao.clear()
    }
}
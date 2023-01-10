package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.CartItemDao
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.cartitems.CartItemWithSubItems
import kotlinx.coroutines.flow.Flow

class CartItemRepository(private val cartItemDao: CartItemDao) {
    val allItems: Flow<List<CartItem>> =
        cartItemDao.getAll()

    fun insert(cartItem: CartItem) {
        cartItemDao.insert(cartItem)
    }

    fun delete(cartItem: CartItem) {
        cartItemDao.delete(cartItem)
    }

    fun clear() {
        cartItemDao.clear()
    }
}
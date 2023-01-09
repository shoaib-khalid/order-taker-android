package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.CartSubItemDao
import com.symplified.ordertaker.models.cartitems.CartSubItem
import kotlinx.coroutines.flow.Flow

class CartSubItemRepository(private val cartSubItemDao: CartSubItemDao) {
    val allCartSubItems: Flow<List<CartSubItem>> = cartSubItemDao.getAllCartSubItems()

    suspend fun insert(cartSubItem: CartSubItem) {
        cartSubItemDao.insert(cartSubItem)
    }

    fun delete(cartSubItem: CartSubItem) {
        cartSubItemDao.delete(cartSubItem)
    }

    suspend fun clear() = cartSubItemDao.deleteAll()
}
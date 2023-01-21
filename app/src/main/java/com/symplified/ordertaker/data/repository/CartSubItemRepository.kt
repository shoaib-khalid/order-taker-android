package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.dao.CartSubItemDao
import com.symplified.ordertaker.models.cartitems.CartSubItem
import kotlinx.coroutines.flow.Flow

class CartSubItemRepository(private val cartSubItemDao: CartSubItemDao) {
    val allCartSubItems: Flow<List<CartSubItem>> = cartSubItemDao.getAllCartSubItems()

    suspend fun insert(cartSubItem: CartSubItem) {
        cartSubItemDao.insert(cartSubItem)
    }

    suspend fun delete(cartSubItem: CartSubItem) {
        cartSubItemDao.deleteByCartItemId(cartSubItem)
    }

    suspend fun clear() = cartSubItemDao.deleteAll()
}
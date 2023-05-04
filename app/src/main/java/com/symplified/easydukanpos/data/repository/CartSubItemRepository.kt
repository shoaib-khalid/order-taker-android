package com.symplified.easydukanpos.data.repository

import com.symplified.easydukanpos.data.dao.CartSubItemDao
import com.symplified.easydukanpos.models.cartitems.CartSubItem
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
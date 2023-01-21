package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.dao.CartItemAddOnDao
import com.symplified.ordertaker.data.dao.CartItemDao
import com.symplified.ordertaker.data.dao.CartSubItemDao
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.cartitems.CartItemAddOn
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import com.symplified.ordertaker.models.cartitems.CartSubItem
import kotlinx.coroutines.flow.Flow

class CartItemRepository(
    private val cartItemDao: CartItemDao,
    private val cartSubItemDao: CartSubItemDao,
    private val cartItemAddOnDao: CartItemAddOnDao
) {
    val allItems: Flow<List<CartItemWithAddOnsAndSubItems>> =
        cartItemDao.getAllCartItemWithAddOnsAndSubItems()

    suspend fun insert(
        cartItem: CartItem,
        cartItemAddOns: List<CartItemAddOn>,
        cartSubItems: List<CartSubItem>
    ) {
        val cartItemId = cartItemDao.insert(cartItem)
        cartItemAddOns.forEach{ cartItemAddOn ->
            cartItemAddOn.cartItemId = cartItemId
            cartItemAddOnDao.insert(cartItemAddOn)
        }
        cartSubItems.forEach{ cartSubItem ->
            cartSubItem.cartItemId = cartItemId
            cartSubItemDao.insert(cartSubItem)
        }
    }

    suspend fun delete(cartItemWithAddOnsAndSubItems: CartItemWithAddOnsAndSubItems) {
        cartItemDao.delete(cartItemWithAddOnsAndSubItems.cartItem)
        cartItemAddOnDao.deleteByCartItemId(cartItemWithAddOnsAndSubItems.cartItem.id)
        cartSubItemDao.deleteByCartItemId(cartItemWithAddOnsAndSubItems.cartItem.id)
    }

    suspend fun clear() {
        cartItemDao.deleteAll()
        cartSubItemDao.deleteAll()
        cartItemAddOnDao.deleteAll()
    }
}
package com.symplified.ordertaker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.symplified.ordertaker.models.cartitems.CartItemAddOn
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemAddOnDao {
    @Query("SELECT * FROM cart_item_add_ons")
    fun getAllCartItemAddOns(): Flow<List<CartItemAddOn>>

    @Insert
    suspend fun insert(cartItemAddOn: CartItemAddOn)

    @Delete
    suspend fun delete(cartItemAddOn: CartItemAddOn)

    @Query("DELETE FROM cart_item_add_ons WHERE cartItemId=:cartItemId")
    suspend fun deleteByCartItemId(cartItemId: Long)

    @Query("DELETE FROM cart_item_add_ons")
    suspend fun deleteAll()
}
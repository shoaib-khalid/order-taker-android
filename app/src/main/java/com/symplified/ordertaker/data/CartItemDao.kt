package com.symplified.ordertaker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.cartitems.CartItemWithSubItems
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Transaction
    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartItemWithSubItems>>

    @Insert
    fun insert(cartItem: CartItem)

    @Delete
    fun delete(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    fun clear()
}
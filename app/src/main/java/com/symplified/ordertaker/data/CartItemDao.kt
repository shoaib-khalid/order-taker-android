package com.symplified.ordertaker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.symplified.ordertaker.models.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartItem>>

    @Insert
    fun insert(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    fun clear()
}
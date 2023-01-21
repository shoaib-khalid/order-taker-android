package com.symplified.ordertaker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.cartitems.CartItemWithAddOnsAndSubItems
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartItem>>

    @Transaction
    @Query("SELECT * FROM cart_items")
    fun getAllCartItemWithAddOnsAndSubItems(): Flow<List<CartItemWithAddOnsAndSubItems>>

    @Insert
    suspend fun insert(cartItem: CartItem): Long

    @Delete
    suspend fun delete(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAll()
}
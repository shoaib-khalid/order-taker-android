package com.symplified.easydukanpos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.symplified.easydukanpos.models.cartitems.CartSubItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartSubItemDao {
    @Query("SELECT * FROM cart_sub_items")
    fun getAllCartSubItems(): Flow<List<CartSubItem>>

    @Insert
    suspend fun insert(cartSubItem: CartSubItem)

    @Delete
    suspend fun deleteByCartItemId(cartSubItem: CartSubItem)

    @Query("DELETE FROM cart_sub_items WHERE cartItemId = :cartItemId")
    suspend fun deleteByCartItemId(cartItemId: Long)

    @Query("DELETE FROM cart_sub_items")
    suspend fun deleteAll()
}
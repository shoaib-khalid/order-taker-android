package com.symplified.ordertaker.data

import androidx.room.*
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.cartitems.CartSubItem
import com.symplified.ordertaker.models.zones.Table
import kotlinx.coroutines.flow.Flow

@Dao
interface CartSubItemDao {
    @Query("SELECT * FROM cart_sub_items")
    fun getAllCartSubItems(): Flow<List<CartSubItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartSubItem: CartSubItem)

    @Delete
    fun delete(cartSubItem: CartSubItem)

    @Query("DELETE FROM cart_sub_items")
    suspend fun deleteAll()
}
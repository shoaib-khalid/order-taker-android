package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.cartitems.CartItem
import com.symplified.easydukanpos.models.cartitems.CartItemWithAddOnsAndSubItems
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartItem>>

    @Transaction
    @Query("SELECT * FROM cart_items")
    fun getAllCartItemsWithDetailsFlow(): Flow<List<CartItemWithAddOnsAndSubItems>>

    @Transaction
    @Query("SELECT * FROM cart_items")
    fun getAllCartItemsWithDetails(): List<CartItemWithAddOnsAndSubItems>

    @Transaction
    @Query("SELECT * FROM cart_items WHERE itemCode=:itemCode AND productId=:productId")
    fun getCartItems(itemCode: String, productId: String): List<CartItemWithAddOnsAndSubItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartItem): Long

    @Delete
    suspend fun delete(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAll()
}
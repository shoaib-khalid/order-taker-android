package com.symplified.ordertaker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.symplified.ordertaker.models.products.inventories.ProductInventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductInventoryItemDao {

    @Query("SELECT * FROM inventory_items")
    fun getAll(): Flow<List<ProductInventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productInventoryItem: ProductInventoryItem)

    @Delete
    fun delete(productInventoryItem: ProductInventoryItem)

    @Query("DELETE FROM inventory_items")
    fun clear()
}
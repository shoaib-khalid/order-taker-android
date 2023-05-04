package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.products.inventories.ProductInventoryItem
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
    suspend fun clear()
}
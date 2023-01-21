package com.symplified.ordertaker.data.dao

import androidx.room.*
import com.symplified.ordertaker.models.products.inventories.ProductInventory
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductInventoryDao {

    @Query("SELECT * FROM product_inventories")
    fun getAll(): Flow<List<ProductInventory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productInventory: ProductInventory)

    @Delete
    fun delete(productInventory: ProductInventory)

    @Query("DELETE FROM product_inventories")
    fun clear()
}
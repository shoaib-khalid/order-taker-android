package com.symplified.ordertaker.data.dao

import androidx.room.*
import com.symplified.ordertaker.models.products.addons.ProductAddOnDetails
import com.symplified.ordertaker.models.products.inventories.ProductInventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductAddOnItemDetailsDao {

    @Query("SELECT * FROM add_on_item_details")
    fun getAll(): Flow<List<ProductAddOnDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productAddOnDetails: ProductAddOnDetails)

    @Delete
    fun delete(productAddOnDetails: ProductAddOnDetails)

    @Query("DELETE FROM add_on_item_details")
    fun clear()
}
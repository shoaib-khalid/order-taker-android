package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.products.addons.ProductAddOnDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductAddOnItemDetailsDao {

    @Query("SELECT * FROM add_on_item_details")
    fun getAll(): Flow<List<ProductAddOnDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productAddOnDetails: ProductAddOnDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productAddOnDetails: List<ProductAddOnDetails>)

    @Delete
    fun delete(productAddOnDetails: ProductAddOnDetails)

    @Query("DELETE FROM add_on_item_details")
    fun clear()
}
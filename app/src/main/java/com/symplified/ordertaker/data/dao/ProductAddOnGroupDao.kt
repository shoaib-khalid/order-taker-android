package com.symplified.ordertaker.data.dao

import androidx.room.*
import com.symplified.ordertaker.models.products.addons.ProductAddOnGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductAddOnGroupDao {

    @Query("SELECT * FROM add_on_groups")
    fun getAll(): Flow<List<ProductAddOnGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productAddOnGroup: ProductAddOnGroup)

    @Delete
    fun delete(productAddOnGroup: ProductAddOnGroup)

    @Query("DELETE FROM add_on_groups")
    fun clear()
}
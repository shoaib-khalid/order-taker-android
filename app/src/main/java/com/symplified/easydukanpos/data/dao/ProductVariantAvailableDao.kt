package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.products.variants.ProductVariantAvailable
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductVariantAvailableDao {

    @Query("SELECT * FROM product_variants_available")
    fun getAll(): Flow<List<ProductVariantAvailable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productVariantAvailable: ProductVariantAvailable)

    @Delete
    fun delete(productVariantAvailable: ProductVariantAvailable)

    @Query("DELETE FROM product_variants_available")
    fun clear()
}
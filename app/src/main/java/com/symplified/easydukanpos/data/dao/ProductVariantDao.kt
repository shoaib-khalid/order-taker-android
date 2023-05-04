package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.products.variants.ProductVariant
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductVariantDao {

    @Query("SELECT * FROM product_variants")
    fun getAll(): Flow<List<ProductVariant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productVariant: ProductVariant)

    @Delete
    fun delete(productVariant: ProductVariant)

    @Query("DELETE FROM product_variants")
    fun clear()
}
package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.products.options.ProductPackageOptionDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductPackageOptionDetailsDao {

    @Query("SELECT * FROM package_option_details")
    fun getAll(): Flow<List<ProductPackageOptionDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productPackageOptionDetails: ProductPackageOptionDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productPackageOptionDetails: List<ProductPackageOptionDetails>)

    @Delete
    fun delete(productPackageOptionDetails: ProductPackageOptionDetails)

    @Query("DELETE FROM package_option_details")
    fun clear()
}
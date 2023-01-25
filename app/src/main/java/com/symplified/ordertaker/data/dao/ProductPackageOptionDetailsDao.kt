package com.symplified.ordertaker.data.dao

import androidx.room.*
import com.symplified.ordertaker.models.products.options.ProductPackageOptionDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductPackageOptionDetailsDao {

    @Query("SELECT * FROM package_option_details")
    fun getAll(): Flow<List<ProductPackageOptionDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productPackageOptionDetails: ProductPackageOptionDetails)

    @Delete
    fun delete(productPackageOptionDetails: ProductPackageOptionDetails)

    @Query("DELETE FROM package_option_details")
    fun clear()
}
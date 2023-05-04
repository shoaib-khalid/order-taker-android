package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.products.options.ProductPackage
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductPackageDao {

    @Query("SELECT * FROM package_options")
    fun getAll(): Flow<List<ProductPackage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productPackage: ProductPackage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(productPackages: List<ProductPackage>)

    @Delete
    fun delete(productPackage: ProductPackage)

    @Query("DELETE FROM package_options")
    fun clear()
}
package com.symplified.ordertaker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.products.ProductWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProductsWithDetails(): Flow<List<ProductWithDetails>>

    @Transaction
    @Query("SELECT * FROM products WHERE categoryId=:categoryId")
    fun getProductsWithCategoryId(categoryId: String) : LiveData<List<ProductWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: Product)

    @Delete
    fun delete(product: Product)

    @Query("DELETE FROM products")
    fun clear()
}
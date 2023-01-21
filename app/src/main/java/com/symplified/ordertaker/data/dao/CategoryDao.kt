package com.symplified.ordertaker.data.dao

import androidx.room.*
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.categories.CategoryWithProducts
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getAllCategoriesWithProducts(): Flow<List<CategoryWithProducts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Query("DELETE FROM categories")
    fun clear()
}
package com.symplified.ordertaker.data.dao

import androidx.room.*
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.categories.CategoryWithProducts
import kotlinx.coroutines.flow.Flow

const val BEST_SELLERS_CATEGORY_ID = "best_sellers"
const val BEST_SELLERS_CATEGORY_NAME = "Best Sellers"

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getAllCategoriesWithProducts(): Flow<List<CategoryWithProducts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Query("DELETE FROM categories WHERE id != '$BEST_SELLERS_CATEGORY_ID'")
    fun clear()
}
package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.categories.Category
import com.symplified.easydukanpos.models.categories.CategoryWithProducts
import kotlinx.coroutines.flow.Flow

const val BEST_SELLERS_CATEGORY_ID = "best_sellers"
const val BEST_SELLERS_CATEGORY_NAME = "Best Sellers"

const val OPEN_ITEMS_CATEGORY_ID = "open_items"
const val OPEN_ITEMS_CATEGORY_NAME = "Open Items"

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getAllCategoriesWithProducts(): Flow<List<CategoryWithProducts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: List<Category>)

    @Query("DELETE FROM categories")
    fun clear()
}
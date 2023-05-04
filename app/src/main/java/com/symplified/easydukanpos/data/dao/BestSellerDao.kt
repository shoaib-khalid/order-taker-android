package com.symplified.easydukanpos.data.dao

import androidx.room.*
import com.symplified.easydukanpos.models.bestsellers.BestSeller
import com.symplified.easydukanpos.models.bestsellers.BestSellerProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface BestSellerDao {

    @Query("SELECT id FROM best_sellers")
    fun getAll(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM best_sellers")
    fun getAllBestSellers(): Flow<List<BestSellerProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bestSellers: List<BestSeller>)

    @Query("DELETE FROM best_sellers")
    fun clear()
}
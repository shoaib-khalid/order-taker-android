package com.symplified.easydukanpos.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.symplified.easydukanpos.models.users.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<User?>

    @Query("SELECT storeId FROM users LIMIT 1")
    fun getStoreId(): String?

    @Query("SELECT accessToken FROM users LIMIT 1")
    fun getAccessToken(): String?

    @Query("SELECT refreshToken FROM users LIMIT 1")
    fun getRefreshToken(): String?

    @Query("UPDATE users SET accessToken = :accessToken, refreshToken = :refreshToken")
    fun setTokens(accessToken: String, refreshToken: String)

    @Query("SELECT currencySymbol FROM users LIMIT 1")
    fun getCurrencySymbol(): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("DELETE FROM users")
    fun clear()
}
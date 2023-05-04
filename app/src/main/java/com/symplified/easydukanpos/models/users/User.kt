package com.symplified.easydukanpos.models.users

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.symplified.easydukanpos.models.stores.BusinessType

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val storeId: String,
    val storeName: String,
    val currencySymbol: String,
    val username: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String,
    @ColumnInfo(defaultValue = "ECOMMERCE")
    val businessType: BusinessType
)
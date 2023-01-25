package com.symplified.ordertaker.models.users

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val storeId: String,
    val storeName: String?,
    val username: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)
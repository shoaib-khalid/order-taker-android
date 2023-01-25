package com.symplified.ordertaker.models.users

import androidx.room.Entity

@Entity(tableName = "users")
data class User(
    val id: String,
    val storeId: String,
    val storeName: String?,
    val username: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)
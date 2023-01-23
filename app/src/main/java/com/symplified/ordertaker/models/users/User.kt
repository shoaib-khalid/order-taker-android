package com.symplified.ordertaker.models.users

data class User(
    val id: String,
    val storeId: String,
    val username: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)
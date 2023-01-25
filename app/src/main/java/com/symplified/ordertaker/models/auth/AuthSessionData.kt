package com.symplified.ordertaker.models.auth

data class AuthSessionData(
    val ownerId: String,
    val username: String,
    val accessToken: String,
    val refreshToken: String
)

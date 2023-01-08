package com.symplified.ordertaker.models.auth

data class AuthSessionData(
    val username: String,
    val expiry: String,
    val created: String,
    val accessToken: String,
    val refreshToken: String
)
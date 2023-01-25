package com.symplified.ordertaker.models.auth

data class AuthRequest(
    val username: String,
    val password: String,
//    val fcmToken: String
)
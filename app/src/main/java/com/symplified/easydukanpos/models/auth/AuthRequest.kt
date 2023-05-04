package com.symplified.easydukanpos.models.auth

data class AuthRequest(
    val username: String,
    val password: String,
    val fcmToken: String
)
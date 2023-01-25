package com.symplified.ordertaker.models.auth

data class TokenRefreshRequest(
    val fcmToken: String
)

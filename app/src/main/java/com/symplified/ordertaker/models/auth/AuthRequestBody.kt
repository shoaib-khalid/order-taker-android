package com.symplified.ordertaker.models.auth

data class AuthRequestBody(
    val username: String,
    val password: String
)
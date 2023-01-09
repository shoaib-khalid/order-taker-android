package com.symplified.ordertaker.models.users

data class UserResponseBody(
    val status: Int,
    val message: String,
    val data: User
)
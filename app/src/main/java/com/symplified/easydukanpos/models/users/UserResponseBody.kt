package com.symplified.easydukanpos.models.users

data class UserResponseBody(
    val status: Int,
    val message: String,
    val data: UserResponseData
)
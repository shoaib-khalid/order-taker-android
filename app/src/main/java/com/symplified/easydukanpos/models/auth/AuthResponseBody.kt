package com.symplified.easydukanpos.models.auth

data class AuthResponseBody(
    val status: Int,
    val message: String,
    val data: AuthDataObject
)
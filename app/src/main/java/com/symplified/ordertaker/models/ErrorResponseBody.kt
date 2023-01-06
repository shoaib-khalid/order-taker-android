package com.symplified.ordertaker.models

data class ErrorResponseBody(
    val status: Int,
    val message: String,
)
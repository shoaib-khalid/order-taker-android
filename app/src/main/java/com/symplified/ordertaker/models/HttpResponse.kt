package com.symplified.ordertaker.models

data class HttpResponse(
    val timestamp: String,
    val status: Int,
    val message: String,
    val path: String
)
package com.symplified.ordertaker.models

import java.sql.Timestamp

data class HttpResponse(
    val timestamp: String,
    val status: Int,
    val message: String,
    val path: String
)
package com.symplified.ordertaker.models.zones

data class ZonesResponseBody(
    val status: Int,
    val message: String,
    val data: List<Zone>
)
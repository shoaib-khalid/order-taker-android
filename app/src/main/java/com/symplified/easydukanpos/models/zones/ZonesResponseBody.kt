package com.symplified.easydukanpos.models.zones

data class ZonesResponseBody(
    val status: Int,
    val message: String,
    val data: List<Zone>
)
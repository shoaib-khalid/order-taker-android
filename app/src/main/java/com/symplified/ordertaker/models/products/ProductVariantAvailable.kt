package com.symplified.ordertaker.models.products

data class ProductVariantAvailable(
    val id: String,
    val value: String,
    val productId: String,
    val productVariantId: String,
)
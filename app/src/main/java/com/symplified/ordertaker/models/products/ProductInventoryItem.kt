package com.symplified.ordertaker.models.products

data class ProductInventoryItem(
    val itemCode: String,
    val productVariantAvailableId: String,
    val productId: String,
    val productVariantAvailable: ProductVariantAvailable
)
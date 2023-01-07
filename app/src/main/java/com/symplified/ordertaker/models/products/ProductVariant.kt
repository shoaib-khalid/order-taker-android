package com.symplified.ordertaker.models.products

data class ProductVariant(
    val id: String,
    val name: String,
    val productVariantsAvailable: List<ProductVariantAvailable>
)
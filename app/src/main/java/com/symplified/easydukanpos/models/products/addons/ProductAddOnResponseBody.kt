package com.symplified.easydukanpos.models.products.addons

data class ProductAddOnResponseBody(
    val status: Int,
    val message: String,
    val data: List<ProductAddOnGroup>
)

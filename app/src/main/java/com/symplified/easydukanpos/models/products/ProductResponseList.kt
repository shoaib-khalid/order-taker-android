package com.symplified.easydukanpos.models.products

data class ProductResponseList(
    val content: List<Product>,
    val numberOfElements: Int
)
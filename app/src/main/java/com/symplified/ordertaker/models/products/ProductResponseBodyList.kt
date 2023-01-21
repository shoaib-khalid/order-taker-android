package com.symplified.ordertaker.models.products

data class ProductResponseBodyList(
    val content: List<Product>,
    val numberOfElements: Int
)
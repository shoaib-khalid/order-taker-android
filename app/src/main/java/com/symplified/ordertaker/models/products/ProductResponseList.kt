package com.symplified.ordertaker.models.products

data class ProductResponseList(
    val content: List<Product>,
    val numberOfElements: Int
)
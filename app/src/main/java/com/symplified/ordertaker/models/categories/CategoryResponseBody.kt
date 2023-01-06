package com.symplified.ordertaker.models.categories

data class CategoryResponseBody (
    val status: Int,
    val message: String,
    val data: CategoryResponseBodyList
)
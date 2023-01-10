package com.symplified.ordertaker.models.products.options

data class ProductPackageResponseBody(
    val status: Int,
    val message: String,
    val data: List<ProductPackage>
)

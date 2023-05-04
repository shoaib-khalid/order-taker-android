package com.symplified.easydukanpos.models.products.options

data class ProductPackageResponseBody(
    val status: Int,
    val message: String,
    val data: List<ProductPackage>
)

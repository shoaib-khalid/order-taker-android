package com.symplified.ordertaker.models.products.options

data class ProductPackage(
    val id: String,
    val packageId: String,
    val title: String,
    val totalAllow: Int,
    val productPackageOptionDetail: List<ProductPackageOptionDetails>
)

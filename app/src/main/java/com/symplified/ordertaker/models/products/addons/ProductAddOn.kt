package com.symplified.ordertaker.models.products.addons

data class ProductAddOn(
    val id: String,
    val title: String,
    val productAddOnItemDetail: List<ProductAddOnDetails>
)

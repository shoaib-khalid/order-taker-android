package com.symplified.ordertaker.models.products.options

import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.products.ProductInventory

data class ProductPackageOptionDetails(
    val id: String,
    val productPackageOptionId: String,
    val productId: String,
    val product: Product,
    val productInventory: List<ProductInventory>
)

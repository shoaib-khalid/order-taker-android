package com.symplified.ordertaker.models.products

import com.symplified.ordertaker.models.products.addons.ProductAddOn
import com.symplified.ordertaker.models.products.options.ProductPackage

data class ProductWithPackagesAndAddOns(
    val product: Product,
    val packages: MutableList<ProductPackage> = mutableListOf(),
    val addOns: MutableList<ProductAddOn> = mutableListOf()
)

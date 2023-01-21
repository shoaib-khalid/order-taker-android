package com.symplified.ordertaker.models.products.variants

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "product_variants")
data class ProductVariant(
    @PrimaryKey
    val id: String,
    val name: String,
    var productId: String?,
    @Ignore
    val productVariantsAvailable: List<ProductVariantAvailable>
) {
    constructor(
        id: String,
        name: String,
        productId: String
    ): this(id, name, productId, listOf())
}
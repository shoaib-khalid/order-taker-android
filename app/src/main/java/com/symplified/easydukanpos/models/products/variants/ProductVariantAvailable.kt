package com.symplified.easydukanpos.models.products.variants

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_variants_available")
data class ProductVariantAvailable(
    @PrimaryKey
    val id: String,
    val value: String,
    val productId: String,
    val productVariantId: String,
    val sequenceNumber: Int
)
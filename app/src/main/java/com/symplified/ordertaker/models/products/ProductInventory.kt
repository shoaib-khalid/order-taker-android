package com.symplified.ordertaker.models.products

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ProductInventory(
    val itemCode: String,
    val dineInPrice: Double,
    val productId: String,
    val status: ProductStatus,
    val sku: String,
    val productInventoryItems: List<ProductInventoryItem>
)
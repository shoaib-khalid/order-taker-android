package com.symplified.easydukanpos.models.products.inventories

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.symplified.easydukanpos.models.products.variants.ProductVariantAvailable

@Entity(tableName = "inventory_items")
data class ProductInventoryItem(
    @PrimaryKey
    val itemCode: String,
    val sequenceNumber: Int,
    val productVariantAvailableId: String,
    val productId: String,
    @Ignore
    val productVariantAvailable: ProductVariantAvailable?
) {
    constructor(
        itemCode: String,
        sequenceNumber: Int,
        productVariantAvailableId: String,
        productId: String
    ) : this(itemCode, sequenceNumber, productVariantAvailableId, productId, null)
}
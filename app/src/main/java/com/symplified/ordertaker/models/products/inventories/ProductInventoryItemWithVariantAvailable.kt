package com.symplified.ordertaker.models.products.inventories

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.ordertaker.models.products.variants.ProductVariantAvailable

data class ProductInventoryItemWithVariantAvailable(
    @Embedded val inventoryItem: ProductInventoryItem,
    @Relation(
        parentColumn = "productVariantAvailableId",
        entityColumn = "id"
    )
    val productVariantAvailable: ProductVariantAvailable
)

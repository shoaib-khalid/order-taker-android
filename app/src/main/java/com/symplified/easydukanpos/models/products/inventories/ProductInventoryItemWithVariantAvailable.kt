package com.symplified.easydukanpos.models.products.inventories

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.easydukanpos.models.products.variants.ProductVariantAvailable

data class ProductInventoryItemWithVariantAvailable(
    @Embedded val inventoryItem: ProductInventoryItem,
    @Relation(
        parentColumn = "productVariantAvailableId",
        entityColumn = "id"
    )
    val productVariantAvailable: ProductVariantAvailable
)

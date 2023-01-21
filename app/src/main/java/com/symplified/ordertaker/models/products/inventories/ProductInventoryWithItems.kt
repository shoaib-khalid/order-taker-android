package com.symplified.ordertaker.models.products.inventories

import androidx.room.Embedded
import androidx.room.Relation

data class ProductInventoryWithItems(
    @Embedded val productInventory: ProductInventory,
    @Relation(
        entity = ProductInventoryItem::class,
        parentColumn = "itemCode",
        entityColumn = "itemCode"
    )
    val inventoryItems: List<ProductInventoryItemWithVariantAvailable>
)

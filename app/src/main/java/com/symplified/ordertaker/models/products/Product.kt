package com.symplified.ordertaker.models.products

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.symplified.ordertaker.models.products.inventories.ProductInventory
import com.symplified.ordertaker.models.products.variants.ProductVariant

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val categoryId: String,
    val isPackage: Boolean,
    val hasAddOn: Boolean,
    @Ignore
    val productInventories: List<ProductInventory>,
    @Ignore
    val productVariants: List<ProductVariant>
) {
    constructor(
        id: String,
        name: String,
        categoryId: String,
        isPackage: Boolean,
        hasAddOn: Boolean,
    ) : this(id, name, categoryId, isPackage, hasAddOn, listOf(), listOf())
}
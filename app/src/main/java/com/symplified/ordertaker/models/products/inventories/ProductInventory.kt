package com.symplified.ordertaker.models.products.inventories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.symplified.ordertaker.models.products.ProductStatus

@Entity(tableName = "product_inventories")
data class ProductInventory(
    @PrimaryKey
    val itemCode: String,
    val dineInPrice: Double,
    val productId: String,
    val status: ProductStatus,
    val sku: String,
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER, defaultValue = "1")
    val quantity: Int,
    @Ignore
    val productInventoryItems: List<ProductInventoryItem>
) {
    constructor(
        itemCode: String,
        dineInPrice: Double,
        productId: String,
        status: ProductStatus,
        sku: String,
        quantity: Int
    ) : this(itemCode, dineInPrice, productId, status, sku, quantity, listOf())
}
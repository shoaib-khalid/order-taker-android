package com.symplified.ordertaker.models.products

import androidx.room.ColumnInfo
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
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT, defaultValue = "")
    val description: String,
    var categoryId: String?,
    val isPackage: Boolean,
    val hasAddOn: Boolean,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT, defaultValue = "")
    val thumbnailUrl: String,
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER, defaultValue = "0")
    val sequenceNumber: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER, defaultValue = "1")
    val allowOutOfStockPurchases: Boolean,
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER, defaultValue = "0")
    val isCustomPrice: Boolean,
    @Ignore
    val productInventories: List<ProductInventory>,
    @Ignore
    val productVariants: List<ProductVariant>,
    @Ignore
    val productAssets: List<ProductAsset>
) {
    constructor(
        id: String,
        name: String,
        description: String,
        categoryId: String?,
        isPackage: Boolean,
        hasAddOn: Boolean,
        thumbnailUrl: String,
        sequenceNumber: Int,
        allowOutOfStockPurchases: Boolean,
        isCustomPrice: Boolean,
        ) : this(
        id,
        name,
        description,
        categoryId,
        isPackage,
        hasAddOn,
        thumbnailUrl.split("/").last(),
        sequenceNumber,
        allowOutOfStockPurchases,
        isCustomPrice,
        listOf(),
        listOf(),
        listOf()
    )
}
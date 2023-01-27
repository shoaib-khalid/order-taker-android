package com.symplified.ordertaker.models.products

import android.net.Uri
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
    val categoryId: String,
    val isPackage: Boolean,
    val hasAddOn: Boolean,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT, defaultValue = "")
    val thumbnailUrl: String,
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER, defaultValue = "0")
    val sequenceNumber: Int,
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
        categoryId: String,
        isPackage: Boolean,
        hasAddOn: Boolean,
        thumbnailUrl: String,
        sequenceNumber: Int
    ) : this(
        id,
        name,
        categoryId,
        isPackage,
        hasAddOn,
        thumbnailUrl.split("/").last(),
        sequenceNumber,
        listOf(),
        listOf(),
        listOf()
    )
}
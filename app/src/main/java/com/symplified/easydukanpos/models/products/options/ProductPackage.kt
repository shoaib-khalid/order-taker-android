package com.symplified.easydukanpos.models.products.options

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "package_options")
data class ProductPackage(
    @PrimaryKey
    val id: String,
    val packageId: String,
    val title: String?,
    val totalAllow: Int,
    val minAllow: Int,
    val allowSameItem: Boolean,
    val sequenceNumber: Int,
    @Ignore
    val productPackageOptionDetail: List<ProductPackageOptionDetails>
) {
    constructor(
        id: String,
        packageId: String,
        title: String?,
        totalAllow: Int,
        minAllow: Int,
        allowSameItem: Boolean,
        sequenceNumber: Int
    ): this(id, packageId, title, totalAllow, minAllow, allowSameItem, sequenceNumber, listOf())
}
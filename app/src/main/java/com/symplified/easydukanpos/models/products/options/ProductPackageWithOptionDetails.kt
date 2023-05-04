package com.symplified.easydukanpos.models.products.options

import androidx.room.Embedded
import androidx.room.Relation

data class ProductPackageWithOptionDetails(
    @Embedded val productPackage: ProductPackage,
    @Relation(
        entity = ProductPackageOptionDetails::class,
        parentColumn = "id",
        entityColumn = "productPackageOptionId"
    )
    val productPackageOptionDetails: List<PackageOptionDetailsWithProductAndInventories>
)

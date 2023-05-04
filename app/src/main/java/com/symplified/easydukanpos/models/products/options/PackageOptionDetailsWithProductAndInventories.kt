package com.symplified.easydukanpos.models.products.options

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.easydukanpos.models.products.Product
import com.symplified.easydukanpos.models.products.inventories.ProductInventory

data class PackageOptionDetailsWithProductAndInventories(
    @Embedded val optionDetails: ProductPackageOptionDetails,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product?,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val productInventories: List<ProductInventory>
)

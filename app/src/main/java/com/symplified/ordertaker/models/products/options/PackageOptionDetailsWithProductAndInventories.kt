package com.symplified.ordertaker.models.products.options

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.products.inventories.ProductInventory

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

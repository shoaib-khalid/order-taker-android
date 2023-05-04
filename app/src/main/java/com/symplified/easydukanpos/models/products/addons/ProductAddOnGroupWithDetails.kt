package com.symplified.easydukanpos.models.products.addons

import androidx.room.Embedded
import androidx.room.Relation

data class ProductAddOnGroupWithDetails(
    @Embedded val productAddOnGroup: ProductAddOnGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "productAddonGroupId"
    )
    val addOnDetails: List<ProductAddOnDetails>
)

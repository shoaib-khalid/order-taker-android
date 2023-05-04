package com.symplified.easydukanpos.models.products.variants

import androidx.room.Embedded
import androidx.room.Relation

data class ProductVariantWithVariantsAvailable(
    @Embedded val productVariant: ProductVariant,
    @Relation(
        parentColumn = "id",
        entityColumn = "productVariantId"
    )
    val variantsAvailable: List<ProductVariantAvailable>
)

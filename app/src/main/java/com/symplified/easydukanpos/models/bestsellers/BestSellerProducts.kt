package com.symplified.easydukanpos.models.bestsellers

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.easydukanpos.models.products.Product
import com.symplified.easydukanpos.models.products.ProductWithDetails

data class BestSellerProduct(
    @Embedded val bestSeller: BestSeller,
    @Relation(
        entity = Product::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val productWithDetails: ProductWithDetails?
)

package com.symplified.ordertaker.models.bestsellers

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.products.ProductWithDetails

data class BestSellerProduct(
    @Embedded val bestSeller: BestSeller,
    @Relation(
        entity = Product::class,
        parentColumn = "id",
        entityColumn = "id"
    )
    val productWithDetails: ProductWithDetails?
)

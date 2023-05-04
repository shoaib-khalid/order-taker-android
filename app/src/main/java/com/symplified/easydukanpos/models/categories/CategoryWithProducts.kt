package com.symplified.easydukanpos.models.categories

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.easydukanpos.models.products.Product
import com.symplified.easydukanpos.models.products.ProductWithDetails

data class CategoryWithProducts(
    @Embedded val category: Category,
    @Relation(
        entity = Product::class,
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val productWithDetails: List<ProductWithDetails>
)

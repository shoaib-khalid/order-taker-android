package com.symplified.ordertaker.models.products

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "products")
data class Product(
//    @PrimaryKey(autoGenerate = true)
    val id: String,
    val name: String,
    val categoryId: String,
    val status: ProductStatus,
    val productInventories: List<ProductInventory>
)
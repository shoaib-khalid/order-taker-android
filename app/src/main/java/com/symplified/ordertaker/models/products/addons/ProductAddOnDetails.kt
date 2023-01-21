package com.symplified.ordertaker.models.products.addons

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "add_on_item_details")
data class ProductAddOnDetails(
    @PrimaryKey
    val id: String,
    val name: String,
    val productId: String,
    val dineInPrice: Double,
    val sequenceNumber: Int,
    val productAddonGroupId: String
)

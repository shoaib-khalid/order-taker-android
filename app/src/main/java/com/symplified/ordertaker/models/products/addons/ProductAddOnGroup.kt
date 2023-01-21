package com.symplified.ordertaker.models.products.addons

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "add_on_groups")
data class ProductAddOnGroup(
    @PrimaryKey
    val id: String,
    val title: String,
    var productId: String?,
    val minAllowed: Int,
    val maxAllowed: Int,
    val sequenceNumber: Int,
    @Ignore
    val productAddOnItemDetail: List<ProductAddOnDetails>
) {
    constructor(
        id: String,
        title: String,
        productId: String,
        minAllowed: Int,
        maxAllowed: Int,
        sequenceNumber: Int,
    ): this(id, title, productId, minAllowed, maxAllowed, sequenceNumber, listOf())
}

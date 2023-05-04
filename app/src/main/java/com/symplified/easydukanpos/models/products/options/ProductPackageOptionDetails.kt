package com.symplified.easydukanpos.models.products.options

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.symplified.easydukanpos.models.products.Product
import com.symplified.easydukanpos.models.products.inventories.ProductInventory

@Entity(tableName = "package_option_details")
data class ProductPackageOptionDetails(
    @PrimaryKey
    val id: String,
    val productPackageOptionId: String,
    val productId: String,
    val isDefault: Boolean,
    val sequenceNumber: Int,
    @Ignore
    val product: Product?,
    @Ignore
    val productInventory: List<ProductInventory>
) {
    constructor(
        id: String,
        productPackageOptionId: String,
        productId: String,
        isDefault: Boolean,
        sequenceNumber: Int
    ): this(id, productPackageOptionId, productId, isDefault, sequenceNumber, null, listOf())
}

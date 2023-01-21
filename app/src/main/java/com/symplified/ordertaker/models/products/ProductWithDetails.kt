package com.symplified.ordertaker.models.products

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.ordertaker.models.products.addons.ProductAddOnGroup
import com.symplified.ordertaker.models.products.addons.ProductAddOnGroupWithDetails
import com.symplified.ordertaker.models.products.inventories.ProductInventory
import com.symplified.ordertaker.models.products.inventories.ProductInventoryWithItems
import com.symplified.ordertaker.models.products.options.ProductPackage
import com.symplified.ordertaker.models.products.options.ProductPackageWithOptionDetails
import com.symplified.ordertaker.models.products.variants.ProductVariant
import com.symplified.ordertaker.models.products.variants.ProductVariantWithVariantsAvailable

data class ProductWithDetails(
    @Embedded val product: Product,
    @Relation(
        entity = ProductInventory::class,
        parentColumn = "id",
        entityColumn = "productId"
    )
    val productInventoriesWithItems: List<ProductInventoryWithItems>,
    @Relation(
        entity = ProductVariant::class,
        parentColumn = "id",
        entityColumn = "productId"
    )
    val productVariantsWithVariantsAvailable: List<ProductVariantWithVariantsAvailable>,
    @Relation(
        entity = ProductAddOnGroup::class,
        parentColumn = "id",
        entityColumn = "productId"
    )
    val productAddOnGroupsWithDetails: List<ProductAddOnGroupWithDetails>,
    @Relation(
        entity = ProductPackage::class,
        parentColumn = "id",
        entityColumn = "packageId"
    )
    val productPackages: List<ProductPackageWithOptionDetails>
)

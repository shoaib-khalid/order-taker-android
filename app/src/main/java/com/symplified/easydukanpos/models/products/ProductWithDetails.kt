package com.symplified.easydukanpos.models.products

import androidx.room.Embedded
import androidx.room.Relation
import com.symplified.easydukanpos.models.products.addons.ProductAddOnGroup
import com.symplified.easydukanpos.models.products.addons.ProductAddOnGroupWithDetails
import com.symplified.easydukanpos.models.products.inventories.ProductInventory
import com.symplified.easydukanpos.models.products.inventories.ProductInventoryWithItems
import com.symplified.easydukanpos.models.products.options.ProductPackage
import com.symplified.easydukanpos.models.products.options.ProductPackageWithOptionDetails
import com.symplified.easydukanpos.models.products.variants.ProductVariant
import com.symplified.easydukanpos.models.products.variants.ProductVariantWithVariantsAvailable

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

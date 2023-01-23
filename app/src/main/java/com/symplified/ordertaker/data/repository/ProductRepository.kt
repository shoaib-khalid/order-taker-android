package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.dao.*
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.categories.CategoryWithProducts
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.models.products.addons.ProductAddOnGroup
import com.symplified.ordertaker.models.products.options.ProductPackage
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val productInventoryDao: ProductInventoryDao,
    private val productInventoryItemDao: ProductInventoryItemDao,
    private val productVariantDao: ProductVariantDao,
    private val productVariantAvailableDao: ProductVariantAvailableDao,
    private val productAddOnGroupDao: ProductAddOnGroupDao,
    private val productAddOnItemDetailsDao: ProductAddOnItemDetailsDao,
    private val productPackageDao: ProductPackageDao,
    private val productPackageOptionDetailsDao: ProductPackageOptionDetailsDao
) {
    val allCategoriesWithProducts: Flow<List<CategoryWithProducts>> =
        categoryDao.getAllCategoriesWithProducts()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    fun insert(category: Category) = categoryDao.insert(category)

    val allProductsWithDetails: Flow<List<ProductWithDetails>> =
        productDao.getAllProductsWithDetails()

    fun getProductsWithCategory(category: Category) =
        productDao.getProductsWithCategoryId(category.id)

    fun insert(product: Product) {
        productDao.insert(product)
        product.productInventories.forEach { inventory ->
            productInventoryDao.insert(inventory)
            inventory.productInventoryItems.forEach { item ->
                item.productVariantAvailable?.let { variantAvailable ->
                    productVariantAvailableDao.insert(variantAvailable)
                }
                productInventoryItemDao.insert(item)
            }
        }
        product.productVariants.forEach { variant ->
            variant.productId = product.id
            productVariantDao.insert(variant)
            variant.productVariantsAvailable.forEach { variantAvailable ->
                productVariantAvailableDao.insert(variantAvailable)
            }
        }
    }

    fun insert(productAddOnGroup: ProductAddOnGroup) {
        productAddOnGroupDao.insert(productAddOnGroup)
        productAddOnGroup.productAddOnItemDetail.forEach { addOnDetails ->
            productAddOnItemDetailsDao.insert(addOnDetails)
        }
    }

    fun insert(productPackage: ProductPackage) {
        productPackageDao.insert(productPackage)
        productPackage.productPackageOptionDetail.forEach { optionDetails ->
            productPackageOptionDetailsDao.insert(optionDetails)
            optionDetails.product?.let { product ->
                productDao.insert(product)
            }
            optionDetails.productInventory.forEach { productInventory ->
                productInventoryDao.insert(productInventory)
            }
        }
    }

    fun clear() {
        categoryDao.clear()
        productDao.clear()
        productInventoryDao.clear()
        productInventoryItemDao.clear()
        productVariantDao.clear()
        productVariantAvailableDao.clear()
        productAddOnGroupDao.clear()
        productAddOnItemDetailsDao.clear()
        productPackageDao.clear()
        productPackageOptionDetailsDao.clear()
    }
}
package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.App
import com.symplified.ordertaker.data.dao.*
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.categories.CategoryWithProducts
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.models.products.addons.ProductAddOnGroup
import com.symplified.ordertaker.models.products.options.ProductPackage
import com.symplified.ordertaker.models.stores.assets.StoreAsset
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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

    suspend fun insertAddOnGroups(category: Category) = categoryDao.insert(category)

    val allProductsWithDetails: Flow<List<ProductWithDetails>> =
        productDao.getAllProductsWithDetails()

    fun getProductsWithCategory(category: Category) =
        if (category.id == BEST_SELLERS_CATEGORY_ID)
            productDao.getBestSellers()
        else
            productDao.getProductsWithCategoryId(category.id)

    suspend fun insertAddOnGroups(product: Product) {
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

    fun insertAddOnGroups(productAddOnGroups: List<ProductAddOnGroup>) {
        productAddOnGroupDao.insert(productAddOnGroups)
        productAddOnGroups.forEach { addOnGroup ->
            productAddOnItemDetailsDao.insert(addOnGroup.productAddOnItemDetail)
        }
    }

    suspend fun insertProductPackages(productPackages: List<ProductPackage>) {
        productPackageDao.insert(productPackages)
        productPackages.forEach { productPackage ->
            productPackageOptionDetailsDao.insert(productPackage.productPackageOptionDetail)
            productPackage.productPackageOptionDetail.forEach { optionDetails ->
                optionDetails.product?.let { product ->
                    productDao.insert(product)
                }
                productInventoryDao.insert(optionDetails.productInventory)
            }
        }
    }

    suspend fun getStoreAssets(storeId: String): List<StoreAsset> {
        try {
            val assetResponse = ServiceGenerator.createProductService()
                .getStoreAssetsByStoreId(storeId)
            if (assetResponse.isSuccessful) {
                return assetResponse.body()!!.data
            }
        } catch (_: Throwable) {
        }
        return listOf()
    }

    suspend fun clear() {
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

    suspend fun fetchCategories(storeId: String): Boolean {
        try {
            val response =
                ServiceGenerator.createProductService().getCategories(storeId)
            if (response.isSuccessful) {
                categoryDao.insert(response.body()!!.data.content)
                return true
            }
            return false
        } catch (e: Throwable) {
            return false
        }
    }

    suspend fun fetchProducts(storeId: String): Boolean {
        try {
            val productApiService = ServiceGenerator.createProductService()
            val response = productApiService.getProductsByStoreId(storeId)
            if (response.isSuccessful) {
                response.body()?.let { productResponseBody ->
                    productResponseBody.data.content.forEach { product ->
                        App.productRepository.insertAddOnGroups(product)

                        if (product.hasAddOn) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val addOnResponse =
                                        productApiService.getProductAddOns(product.id)
                                    if (addOnResponse.isSuccessful) {
                                        val addOnGroups = addOnResponse.body()!!.data
                                        addOnGroups.forEach { addOnGroup ->
                                            addOnGroup.productId = product.id
                                        }
                                        insertAddOnGroups(addOnGroups)
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                        }

                        if (product.isPackage) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val packageResponse =
                                        productApiService.getProductOptions(storeId, product.id)
                                    if (packageResponse.isSuccessful) {
                                        val packages = packageResponse.body()!!.data
                                        insertProductPackages(packages)
                                    }
                                } catch (_: Throwable) {
                                }
                            }
                        }
                    }
                }
                return true
            }
        } catch (_: Throwable) {
        }
        return false
    }
}
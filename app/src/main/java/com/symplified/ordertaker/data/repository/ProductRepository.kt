package com.symplified.ordertaker.data.repository

import android.util.Log
import com.symplified.ordertaker.App
import com.symplified.ordertaker.data.dao.*
import com.symplified.ordertaker.models.bestsellers.BestSellerProduct
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
    private val productPackageOptionDetailsDao: ProductPackageOptionDetailsDao,
    private val bestSellerDao: BestSellerDao
) {
    val allCategoriesWithProducts: Flow<List<CategoryWithProducts>> =
        categoryDao.getAllCategoriesWithProducts()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val bestSellers: Flow<List<BestSellerProduct>> = bestSellerDao.getAllBestSellers()
    val openItems: Flow<List<ProductWithDetails>> = productDao.getOpenItems()

    suspend fun insertCategory(category: Category) = categoryDao.insert(category)

    val allProductsWithDetails: Flow<List<ProductWithDetails>> =
        productDao.getAllProductsWithDetails()

    fun getProductsWithCategory(category: Category) =
        productDao.getProductsWithCategoryId(category.id)

    private suspend fun insertProduct(product: Product) {
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

    private suspend fun insertCategory(productAddOnGroups: List<ProductAddOnGroup>) {
        productAddOnGroupDao.insert(productAddOnGroups)
        productAddOnGroups.forEach { addOnGroup ->
            productAddOnItemDetailsDao.insert(addOnGroup.productAddOnItemDetail)
        }
    }

    private suspend fun insertProductPackages(productPackages: List<ProductPackage>) {
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
        bestSellerDao.clear()
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
            categoryDao.insert(
                listOf(
                    Category(OPEN_ITEMS_CATEGORY_ID, OPEN_ITEMS_CATEGORY_NAME),
                    Category(BEST_SELLERS_CATEGORY_ID, BEST_SELLERS_CATEGORY_NAME)
                )
            )
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
                        insertProduct(product)

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
                                        insertCategory(addOnGroups)
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

    suspend fun fetchBestSellers(storeId: String): Boolean {
        try {
            val response = ServiceGenerator.createLocationService()
                .getBestSellers(storeId)
            Log.d("best-sellers", "Best Sellers request successful: ${response.isSuccessful}")
            response.body()?.let { responseBody ->
                bestSellerDao.insert(responseBody.data)
                return true
            }
        } catch (_: Throwable) {
        }
        return false
    }

    suspend fun fetchOpenItemProducts(storeId: String): Boolean {
        try {
            val response = ServiceGenerator.createProductService()
                .getOpenItemProductsByStoreId(storeId)
            if (response.isSuccessful) {
                response.body()?.let { productResponseBody ->
                    productResponseBody.data.content.forEach {
                        insertProduct(it)
                    }
                }
                return true
            }
        } catch (_: Throwable) {
        }
        return false
    }
}
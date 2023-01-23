package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.symplified.ordertaker.App
import com.symplified.ordertaker.constants.SharedPrefsKey
import com.symplified.ordertaker.models.ErrorResponseBody
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.categories.CategoryResponseBody
import com.symplified.ordertaker.models.categories.CategoryWithProducts
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables
import com.symplified.ordertaker.models.zones.ZonesResponseBody
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "menu-view-model"

class MenuViewModel : ViewModel() {

    val zonesWithTables: LiveData<List<ZoneWithTables>> = App.zoneRepository.allZones.asLiveData()
    val tables: LiveData<List<Table>> = App.tableRepository.allTables.asLiveData()
    val categories: LiveData<List<Category>> = App.productRepository.allCategories.asLiveData()
    val categoriesWithProducts: LiveData<List<CategoryWithProducts>> =
        App.productRepository.allCategoriesWithProducts.asLiveData()
//    val productsWithDetails: LiveData<List<ProductWithDetails>> = App.productRepository.allProductsWithDetails.asLiveData()

//    val menuItems: LiveData<List<Product>> = OrderTakerAppication.productRepository.allItems.asLiveData()

    var selectedTable: Table? = null

    private val _selectedCategory = MutableLiveData<Category?>().apply { value = null }
    val selectedCategory: LiveData<Category?> = _selectedCategory

    val productsWithDetails: LiveData<List<ProductWithDetails>> = _selectedCategory.switchMap { category ->
        if (category != null) {
            App.productRepository.getProductsWithCategory(category)
        } else {
            App.productRepository.allProductsWithDetails.asLiveData()
        }
    }

    fun insert(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        App.productRepository.insert(category)
    }

    fun insert(zoneWithTables: ZoneWithTables) = CoroutineScope(Dispatchers.IO).launch {
        App.zoneRepository.insert(zoneWithTables.zone)
        zoneWithTables.tables.forEach { table ->
            App.tableRepository.insert(table)
        }
    }

    fun insert(zone: Zone) = CoroutineScope(Dispatchers.IO).launch {
        App.zoneRepository.insert(zone)
        zone.tagTables.forEach { table ->
            App.tableRepository.insert(table)
        }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun clearSelectedCategory() {
        _selectedCategory.value = null
    }

    private val _isLoadingZonesAndTables = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingZonesAndTables: LiveData<Boolean> = _isLoadingZonesAndTables
    fun fetchZonesAndTables() {
        _isLoadingZonesAndTables.value = true
        val storeId = App.sharedPreferences().getString(SharedPrefsKey.STORE_ID, "")!!
        ServiceGenerator.createLocationService()
            .getZones(storeId)
            .clone()
            .enqueue(
                object : Callback<ZonesResponseBody> {
                    override fun onResponse(
                        call: Call<ZonesResponseBody>,
                        response: Response<ZonesResponseBody>
                    ) {
                        _isLoadingZonesAndTables.value = false
                        if (response.isSuccessful) {
                            response.body()?.let { zoneData ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    App.zoneRepository.clear()
                                    App.tableRepository.clear()
                                    zoneData.data.forEach { zone -> insert(zone) }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ZonesResponseBody>, t: Throwable) {
                        _isLoadingZonesAndTables.value = false
                    }
                }
            )
    }

    private val _isLoadingCategories = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingCategories: LiveData<Boolean> = _isLoadingCategories
    fun fetchCategories() {
        _isLoadingCategories.value = true
        val storeId = App.sharedPreferences().getString(SharedPrefsKey.STORE_ID, "")!!
        ServiceGenerator.createProductService()
            .getCategories(storeId)
            .clone()
            .enqueue(
                object : Callback<CategoryResponseBody> {
                    override fun onResponse(
                        call: Call<CategoryResponseBody>,
                        response: Response<CategoryResponseBody>
                    ) {
                        _isLoadingCategories.value = false
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                body.data.content.forEach { category ->
                                    insert(category)
                                }
                            }
                        } else {
                            response.errorBody()?.let { errorBody ->
                                val errorResponse: ErrorResponseBody? = Gson().fromJson(
                                    errorBody.string(),
                                    ErrorResponseBody::class.java
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<CategoryResponseBody>, t: Throwable) {
                        _isLoadingCategories.value = false
                    }
                }
            )
    }

    private val _isLoadingProducts = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingProducts: LiveData<Boolean> = _isLoadingProducts
    private fun setIsLoadingProducts(status: Boolean) = CoroutineScope(Dispatchers.Main).launch {
        _isLoadingProducts.value = status
    }

    fun fetchProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            setIsLoadingProducts(true)

            val productApiService = ServiceGenerator.createProductService()
            val storeId = App.sharedPreferences().getString(SharedPrefsKey.STORE_ID, "")!!
            try {
                val response = productApiService.getProductsByStoreId(storeId)
                if (response.isSuccessful) {

                    response.body()?.let { productResponseBody ->
                        productResponseBody.data.content.forEach { product ->
                            App.productRepository.insert(product)

                            if (product.hasAddOn) {
                                launch {
                                    try {
                                        val addOnResponse = productApiService.getProductAddOns(product.id)
                                        if (addOnResponse.isSuccessful) {
                                            addOnResponse.body()!!.data.forEach { addOnGroup ->
                                                addOnGroup.productId = product.id
                                                App.productRepository.insert(addOnGroup)
                                            }
                                        } else {
                                            Log.e(TAG, "Error ${addOnResponse.code()} when getting addons for ${product.id}")
                                        }
                                    } catch (e: Throwable) {
                                        Log.e(TAG, "Failed to get add-ons for ${product.id}. ${e.localizedMessage}")
                                    }
                                }
                            }

                            if (product.isPackage) {
                                launch {
                                    try {
                                        val packageResponse = productApiService.getProductOptions(storeId, product.id)
                                        if (packageResponse.isSuccessful) {
                                            packageResponse.body()!!.data.forEach { productPackage ->
                                                App.productRepository.insert(productPackage)
                                            }
                                        } else {
                                            Log.e(TAG, "Error ${packageResponse.code()} when getting packages for ${product.id}.")
                                        }
                                    } catch (e: Throwable) {
                                        Log.e(TAG, "Failed to get package. ${e.localizedMessage}")
                                    }
                                }
                            }
                        }
                    }
                    setIsLoadingProducts(false)
                } else {
                    Log.d("menuviewmodel", "Error ${response.code()} when fetching products.")
                    setIsLoadingProducts(false)
                }
            } catch (e: Throwable) {
                Log.e("menuviewmodel", "Failed to get products. ${e.localizedMessage}")
                setIsLoadingProducts(false)
            }
        }
    }
}
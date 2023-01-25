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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val TAG = "menu-view-model"

class MenuViewModel : ViewModel() {

    val zonesWithTables: LiveData<List<ZoneWithTables>> = App.zoneRepository.allZones.asLiveData()
    val tables: LiveData<List<Table>> = App.tableRepository.allTables.asLiveData()
    val categories: LiveData<List<Category>> = App.productRepository.allCategories.asLiveData()

    var selectedTable: Table? = null

    private val _selectedCategory = MutableLiveData<Category?>().apply { value = null }
    val selectedCategory: LiveData<Category?> = _selectedCategory

    val productsWithDetails: LiveData<List<ProductWithDetails>> =
        _selectedCategory.switchMap { category ->
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
        CoroutineScope(Dispatchers.IO).launch {
            App.userRepository.user.collect { user ->
                Log.d("menuviewmodel", "User: ${user?.name?: "null"}")
                if (user != null) {
                    try {
                        val response =
                            ServiceGenerator.createLocationService().getZones(user.storeId)
                        if (response.isSuccessful) {
                            response.body()?.let { zoneData ->
                                App.zoneRepository.clear()
                                App.tableRepository.clear()
                                zoneData.data.forEach { zone ->
                                    Log.d("menuviewmodel", zone.zoneName)
                                    insert(zone)
                                }
                            }
                        }
                    } catch (_: Throwable) { }
                    withContext(Dispatchers.Main) {
                        _isLoadingZonesAndTables.value = false
                    }
                }
            }
        }
    }

    private val _isLoadingCategories = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingCategories: LiveData<Boolean> = _isLoadingCategories
    fun fetchCategories() {
        _isLoadingCategories.value = true

        CoroutineScope(Dispatchers.IO).launch {
            App.userRepository.user.collect { user ->
                if (user != null) {
                    try {
                        val response =
                            ServiceGenerator.createProductService().getCategories(user.storeId)
                        response.body()?.let { body ->
                            body.data.content.forEach { category ->
                                insert(category)
                            }
                        }
                    } catch (e: Throwable) {
                        Log.e("menuviewmodel", e.localizedMessage!!)
                    }
                }
                withContext(Dispatchers.Main) {
                    _isLoadingCategories.value = false
                }
            }
        }

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
            App.userRepository.user.collect { user ->
                if (user != null) {
                    try {
                        val response = productApiService.getProductsByStoreId(user.storeId)
                        if (response.isSuccessful) {
                            response.body()?.let { productResponseBody ->
                                productResponseBody.data.content.forEach { product ->
                                    App.productRepository.insert(product)

                                    if (product.hasAddOn) {
                                        launch {
                                            try {
                                                val addOnResponse =
                                                    productApiService.getProductAddOns(product.id)
                                                if (addOnResponse.isSuccessful) {
                                                    addOnResponse.body()!!.data.forEach { addOnGroup ->
                                                        addOnGroup.productId = product.id
                                                        App.productRepository.insert(addOnGroup)
                                                    }
                                                }
                                            } catch (_: Throwable) {}
                                        }
                                    }

                                    if (product.isPackage) {
                                        launch {
                                            try {
                                                val packageResponse =
                                                    productApiService.getProductOptions(
                                                        user.storeId,
                                                        product.id
                                                    )
                                                if (packageResponse.isSuccessful) {
                                                    packageResponse.body()!!.data.forEach { productPackage ->
                                                        App.productRepository.insert(productPackage)
                                                    }
                                                }
                                            } catch (_: Throwable) {}
                                        }
                                    }
                                }
                            }
                            setIsLoadingProducts(false)
                        } else {
                            setIsLoadingProducts(false)
                        }
                    } catch (_: Throwable) { setIsLoadingProducts(false) }
                }
            }
        }
    }
}
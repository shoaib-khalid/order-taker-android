package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.symplified.ordertaker.OrderTakerApplication
import com.symplified.ordertaker.data.repository.CategoryRepository
import com.symplified.ordertaker.data.repository.ProductRepository
import com.symplified.ordertaker.data.repository.TableRepository
import com.symplified.ordertaker.data.repository.ZoneRepository
import com.symplified.ordertaker.models.ErrorResponseBody
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.categories.CategoryResponseBody
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables
import com.symplified.ordertaker.models.zones.ZonesResponseBody
import com.symplified.ordertaker.networking.apis.LocationApi
import com.symplified.ordertaker.networking.apis.ProductApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel(
    private val tableRepository: TableRepository,
    private val zoneRepository: ZoneRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    val zonesWithTables: LiveData<List<ZoneWithTables>> = zoneRepository.allZones.asLiveData()
    val tables: LiveData<List<Table>> = tableRepository.allTables.asLiveData()
    val categories: LiveData<List<Category>> = categoryRepository.allItems.asLiveData()
//    val menuItems: LiveData<List<Product>> = productRepository.allItems.asLiveData()

    private val _currentCategory: MutableLiveData<Category> by lazy {
        MutableLiveData<Category>()
    }
    val currentCategory: LiveData<Category> = _currentCategory

    fun insert(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        categoryRepository.insert(category)
    }

    fun clearAllCategories() = CoroutineScope(Dispatchers.IO).launch {
        categoryRepository.clear()
    }

    fun insert(zoneWithTables: ZoneWithTables) = CoroutineScope(Dispatchers.IO).launch {
        zoneRepository.insert(zoneWithTables.zone)
        zoneWithTables.tables.forEach { table ->
            tableRepository.insert(table)
        }
    }

    fun insert(zone: Zone) = CoroutineScope(Dispatchers.IO).launch {
        zoneRepository.insert(zone)
        zone.tagTables.forEach { table ->
            tableRepository.insert(table)
        }
    }

    fun setCurrentCategory(category: Category) {
        _currentCategory.value = category
    }

    private val _isLoadingZonesAndTables = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingZonesAndTables : LiveData<Boolean> = _isLoadingZonesAndTables
    fun getZonesAndTables(locationApi: LocationApi) {
        _isLoadingZonesAndTables.value = true
        locationApi
            .getZones(OrderTakerApplication.testStoreId)
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
                                zoneData.data.forEach { zone -> insert(zone) }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ZonesResponseBody>, t: Throwable) {
                        _isLoadingZonesAndTables.value = false
                        Log.d("zones", "onFailure ${t.localizedMessage}")
                    }
                }
            )
    }

    private val _isLoadingCategories = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingCategories : LiveData<Boolean> = _isLoadingCategories
    fun getCategories(productApi: ProductApi) {
        _isLoadingCategories.value = true
        productApi
            .getCategories(OrderTakerApplication.testStoreId)
            .clone()
            .enqueue(
                object : Callback<CategoryResponseBody> {
                    override fun onResponse(
                        call: Call<CategoryResponseBody>,
                        response: Response<CategoryResponseBody>
                    ) {
                        Log.d("categories", "Category response raw: ${response.raw()}")
                        _isLoadingCategories.value = false
                        if (response.isSuccessful) {
                            response.body()?.let { body ->
                                Log.d("categories", "${body.data.content.toString()}")
                                body.data.content.forEach { category ->
                                    insert(category)
                                }
                            }
                        } else {
                            response.errorBody()?.let { errorBody ->
                                val gson = Gson()
                                val type = object : TypeToken<CategoryResponseBody>() {}.type
                                val errorResponse: ErrorResponseBody? = gson.fromJson(errorBody.string(), ErrorResponseBody::class.java)
                                Log.d("categories", "Error response: $errorResponse")
                            }
                        }
                    }

                    override fun onFailure(call: Call<CategoryResponseBody>, t: Throwable) {
                        _isLoadingCategories.value = false
                    }
                }
            )
    }
}

class MenuViewModelFactory(
    private val tableRepository: TableRepository,
    private val zoneRepository: ZoneRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return MenuViewModel(
                tableRepository,
                zoneRepository,
                categoryRepository,
                productRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.symplified.ordertaker.App
import com.symplified.ordertaker.data.repository.CategoryRepository
import com.symplified.ordertaker.data.repository.ProductRepository
import com.symplified.ordertaker.data.repository.TableRepository
import com.symplified.ordertaker.data.repository.ZoneRepository
import com.symplified.ordertaker.models.ErrorResponseBody
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.categories.CategoryResponseBody
import com.symplified.ordertaker.models.products.ProductResponseBody
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

class MenuViewModel : ViewModel() {
    val zonesWithTables: LiveData<List<ZoneWithTables>> = App.zoneRepository.allZones.asLiveData()
    val tables: LiveData<List<Table>> = App.tableRepository.allTables.asLiveData()
    val categories: LiveData<List<Category>> = App.categoryRepository.allItems.asLiveData()
//    val menuItems: LiveData<List<Product>> = OrderTakerAppication.productRepository.allItems.asLiveData()

    private val _currentCategory: MutableLiveData<Category> by lazy {
        MutableLiveData<Category>()
    }
    val currentCategory: LiveData<Category> = _currentCategory

    fun insert(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        App.categoryRepository.insert(category)
    }

    fun clearAllCategories() = CoroutineScope(Dispatchers.IO).launch {
        App.categoryRepository.clear()
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

    private val _isLoadingZonesAndTables = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingZonesAndTables : LiveData<Boolean> = _isLoadingZonesAndTables
    fun getZonesAndTables() {
        _isLoadingZonesAndTables.value = true
        ServiceGenerator.createLocationService()
            .getZones(App.testStoreId)
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
    fun getCategories() {
        _isLoadingCategories.value = true
        ServiceGenerator.createProductService()
            .getCategories(App.testStoreId)
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
                                val errorResponse: ErrorResponseBody? = Gson().fromJson(errorBody.string(), ErrorResponseBody::class.java)
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

    private val _products: MutableLiveData<Product> by lazy {
        MutableLiveData<Product>()
    }
    val products: LiveData<Product> = _products
    fun setCurrentCategory(category: Category) {
        _currentCategory.value = category
        ServiceGenerator.createProductService()
            .getProductsByCategoryId(App.testStoreId, category.id)
            .clone()
            .enqueue(
                object: Callback<ProductResponseBody> {
                    override fun onResponse(
                        call: Call<ProductResponseBody>,
                        response: Response<ProductResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { productResponseBody ->
                                Log.d("products", "Product response size: ${productResponseBody.data.content.size}")
                                if (productResponseBody.data.content.isNotEmpty())
                                    _products.value = productResponseBody.data.content[0]
                            }
                        }
                    }

                    override fun onFailure(call: Call<ProductResponseBody>, t: Throwable) {
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
            return MenuViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
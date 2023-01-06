package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.symplified.ordertaker.OrderTakerApplication
import com.symplified.ordertaker.data.repository.CategoryRepository
import com.symplified.ordertaker.data.repository.MenuItemRepository
import com.symplified.ordertaker.data.repository.TableRepository
import com.symplified.ordertaker.data.repository.ZoneRepository
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.MenuItem
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables
import com.symplified.ordertaker.models.zones.ZonesResponseBody
import com.symplified.ordertaker.networking.apis.LocationApi
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
    private val menuItemRepository: MenuItemRepository,
) : ViewModel() {
    val zonesWithTables: LiveData<List<ZoneWithTables>> = zoneRepository.allZones.asLiveData()
    val tables: LiveData<List<Table>> = tableRepository.allTables.asLiveData()
    val categories: LiveData<List<Category>> = categoryRepository.allItems.asLiveData()
    val menuItems: LiveData<List<MenuItem>> = menuItemRepository.allItems.asLiveData()

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

    fun insert(menuItem: MenuItem) = CoroutineScope(Dispatchers.IO).launch {
        menuItemRepository.insert(menuItem)
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
                        Log.d("zones", "Raw response: ${response.raw()}")
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
}

class MenuViewModelFactory(
    private val tableRepository: TableRepository,
    private val zoneRepository: ZoneRepository,
    private val categoryRepository: CategoryRepository,
    private val menuItemRepository: MenuItemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return MenuViewModel(
                tableRepository,
                zoneRepository,
                categoryRepository,
                menuItemRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
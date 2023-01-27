package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.*
import com.symplified.ordertaker.App
import com.symplified.ordertaker.data.dao.BEST_SELLERS_CATEGORY_ID
import com.symplified.ordertaker.data.dao.BEST_SELLERS_CATEGORY_NAME
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.ZoneWithTables
import com.symplified.ordertaker.networking.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG = "menu-view-model"

class MenuViewModel : ViewModel() {

    val zonesWithTables: LiveData<List<ZoneWithTables>> = App.zoneRepository.allZones.asLiveData()
    val categories: LiveData<List<Category>> = App.productRepository.allCategories.asLiveData()

    var selectedTable: Table? = null

    private val _selectedCategory = MutableLiveData<Category?>().apply { value = null }
    val selectedCategory: LiveData<Category?> = _selectedCategory

    val productsWithDetails: LiveData<List<ProductWithDetails>> =
        _selectedCategory.switchMap { category ->
            if (category != null)
                App.productRepository.getProductsWithCategory(category)
            else
                App.productRepository.allProductsWithDetails.asLiveData()
        }

    fun insert(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        App.productRepository.insertAddOnGroups(category)
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun clearSelectedCategory() {
        _selectedCategory.value = null
    }

    private val _isLoadingZonesAndTables = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingZonesAndTables: LiveData<Boolean> = _isLoadingZonesAndTables

    private val _isLoadingCategories = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingCategories: LiveData<Boolean> = _isLoadingCategories
    fun fetchCategories() {
        _isLoadingCategories.value = true

        CoroutineScope(Dispatchers.IO).launch {
            insert(Category(BEST_SELLERS_CATEGORY_ID, BEST_SELLERS_CATEGORY_NAME))

            App.userRepository.user.collect { user ->
                if (user != null) {
                    App.productRepository.fetchCategories(user.storeId)
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

    fun fetchProducts() = CoroutineScope(Dispatchers.IO).launch {
        setIsLoadingProducts(true)

        App.userRepository.user.collect { user ->
            user?.let {
                App.productRepository.fetchProducts(user.storeId)
            }
            setIsLoadingProducts(false)
        }
    }

    fun fetchZonesAndTables() {
        _isLoadingZonesAndTables.value = true

        CoroutineScope(Dispatchers.IO).launch {
            App.userRepository.user.collect { user ->
                if (user != null) {
                    App.zoneRepository.fetchZonesAndTables(user.storeId)
                }

                withContext(Dispatchers.Main) {
                    _isLoadingZonesAndTables.value = false
                }
            }
        }
    }
}
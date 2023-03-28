package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.*
import com.symplified.ordertaker.App
import com.symplified.ordertaker.data.dao.BEST_SELLERS_CATEGORY_ID
import com.symplified.ordertaker.data.dao.BEST_SELLERS_CATEGORY_NAME
import com.symplified.ordertaker.data.dao.OPEN_ITEMS_CATEGORY_ID
import com.symplified.ordertaker.data.dao.OPEN_ITEMS_CATEGORY_NAME
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.products.ProductWithDetails
import com.symplified.ordertaker.models.zones.Table
import com.symplified.ordertaker.models.zones.ZoneWithTables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG = "menu-view-model"

class MenuViewModel : ViewModel() {

    val currencySymbol: LiveData<String?> = App.userRepository.currencySymbol.asLiveData()
    val zonesWithTables: LiveData<List<ZoneWithTables>> = App.zoneRepository.allZones.asLiveData()
    val categories: LiveData<List<Category>> = App.productRepository.allCategories.asLiveData()

    private val bestSellers = MutableLiveData<List<ProductWithDetails>>().apply { value = listOf() }

    //    private val openItems = MutableLiveData<List<ProductWithDetails>>().apply { value = listOf() }
    private val openItems: LiveData<List<ProductWithDetails>> =
        App.productRepository.openItems.asLiveData()

    private val _selectedTable = MutableLiveData<Table?>().apply { value = null }
    val selectedTable: LiveData<Table?> = _selectedTable

    private val _selectedCategory = MutableLiveData<Category?>().apply { value = null }
    val selectedCategory: LiveData<Category?> = _selectedCategory

    private val _scannedBarcode = MutableLiveData<String?>().apply { value = null }
    val scannedBarcode: LiveData<String?> = _scannedBarcode

    init {
        viewModelScope.launch {
            App.productRepository.bestSellers.collect {
                bestSellers.value = it
                    .filter { bestSellerProduct -> bestSellerProduct.productWithDetails != null }
                    .map { bestSellerProduct -> bestSellerProduct.productWithDetails!! }
            }
        }
    }

    fun setSelectedTable(table: Table) {
        _selectedTable.value = table
    }

    val productsWithDetails: LiveData<List<ProductWithDetails>> =
        _selectedCategory.switchMap { category ->
            if (category != null)
                when (category.id) {
                    BEST_SELLERS_CATEGORY_ID -> bestSellers
                    OPEN_ITEMS_CATEGORY_ID -> openItems
                    else -> App.productRepository.getProductsWithCategory(category)
                }
            else
                App.productRepository.allProductsWithDetails.asLiveData()
        }

    fun insert(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        App.productRepository.insertCategory(category)
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
            insert(Category(OPEN_ITEMS_CATEGORY_ID, OPEN_ITEMS_CATEGORY_NAME))
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

    fun setScannedBarcode(barcode: String) {
        viewModelScope.launch {
            _scannedBarcode.value = barcode
            _scannedBarcode.value = null
        }
    }

    private val _isLoadingProducts = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingProducts: LiveData<Boolean> = _isLoadingProducts

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
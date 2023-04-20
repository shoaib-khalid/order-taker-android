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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

const val TAG = "menu-view-model"

class MenuViewModel : ViewModel() {

    val currencySymbol = App.userRepository.currencySymbol
    val zonesWithTables: LiveData<List<ZoneWithTables>> = App.zoneRepository.allZones.asLiveData()
    val categories: LiveData<List<Category>> = App.productRepository.allCategories.asLiveData()

    private val bestSellers: MutableStateFlow<List<ProductWithDetails>> = MutableStateFlow(listOf())

    private val _selectedTable = MutableLiveData<Table?>().apply { value = null }
    val selectedTable: LiveData<Table?> = _selectedTable

    private val _selectedCategory: MutableStateFlow<Category?> = MutableStateFlow(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val productsWithDetails2: Flow<List<ProductWithDetails>> =
        _selectedCategory.flatMapLatest { category ->
            if (category != null)
                when (category.id) {
                    BEST_SELLERS_CATEGORY_ID -> bestSellers
                    OPEN_ITEMS_CATEGORY_ID -> App.productRepository.openItems
                    else -> App.productRepository.getProductsWithCategory2(category)
                }
            else
                App.productRepository.allProductsWithDetails
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
}
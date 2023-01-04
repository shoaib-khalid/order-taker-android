package com.symplified.ordertaker.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.symplified.ordertaker.data.repository.CategoryRepository
import com.symplified.ordertaker.data.repository.MenuItemRepository
import com.symplified.ordertaker.models.Category
import com.symplified.ordertaker.models.MenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuViewModel(
    private val categoryRepository: CategoryRepository,
    private val menuItemRepository: MenuItemRepository
) : ViewModel() {
    val categories: LiveData<List<Category>> = categoryRepository.allItems.asLiveData()
    val menuItems: LiveData<List<MenuItem>> = menuItemRepository.allItems.asLiveData()

    interface CurrentCategoryObserver {
        fun onCurrentCategoryChanged(category: Category)
    }

    val currentCategory: MutableLiveData<Category> by lazy {
        MutableLiveData<Category>()
    }

    //    var currentCategory: Category = Category(name = "")
    fun setCurrentCategory(category: Category) {
//        currentCategory.value = category
        viewModelScope.launch {
            currentCategory.value = category
        }

        Log.d(
            "categories",
            "ViewModel: New Category set. Current Category has observers: ${currentCategory.hasObservers()}" +
                    "Current Category has active observers: ${currentCategory.hasActiveObservers()}"
        )
    }

    fun insert(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        categoryRepository.insert(category)
    }

    fun insert(menuItem: MenuItem) = CoroutineScope(Dispatchers.IO).launch {
        menuItemRepository.insert(menuItem)
    }

    fun clearAllCategories() = CoroutineScope(Dispatchers.IO).launch {
        categoryRepository.clear()
    }
}

class MenuViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val menuItemRepository: MenuItemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return MenuViewModel(categoryRepository, menuItemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
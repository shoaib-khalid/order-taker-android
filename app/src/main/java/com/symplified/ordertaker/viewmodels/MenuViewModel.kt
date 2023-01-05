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

    fun insert(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        categoryRepository.insert(category)
    }

    fun clearAllCategories() = CoroutineScope(Dispatchers.IO).launch {
        categoryRepository.clear()
    }

    fun insert(menuItem: MenuItem) = CoroutineScope(Dispatchers.IO).launch {
        menuItemRepository.insert(menuItem)
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
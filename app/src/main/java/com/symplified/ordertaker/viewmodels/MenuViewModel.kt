package com.symplified.ordertaker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.symplified.ordertaker.data.repository.CategoryRepository
import com.symplified.ordertaker.models.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuViewModel(private val repository: CategoryRepository): ViewModel() {
    val categories: LiveData<List<Category>> = repository.allItems.asLiveData()

    fun insert(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        repository.insert(category)
    }

    fun clearAll() = CoroutineScope(Dispatchers.IO).launch {
        repository.clear()
    }
}

class MenuViewModelFactory(private val repository: CategoryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return MenuViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
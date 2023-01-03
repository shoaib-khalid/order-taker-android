package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.CategoryDao
import com.symplified.ordertaker.models.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    val allItems:  Flow<List<Category>> = categoryDao.getAll()

    fun insert(category: Category) = categoryDao.insert(category)

    fun clear() = categoryDao.clear()
}
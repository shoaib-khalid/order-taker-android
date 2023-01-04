package com.symplified.ordertaker.data.repository

import com.symplified.ordertaker.data.MenuItemDao
import com.symplified.ordertaker.models.MenuItem
import kotlinx.coroutines.flow.Flow

class MenuItemRepository(private val menuItemDao: MenuItemDao) {
    val allItems: Flow<List<MenuItem>> = menuItemDao.getAll()

    fun insert(menuItem: MenuItem) = menuItemDao.insert(menuItem)
}
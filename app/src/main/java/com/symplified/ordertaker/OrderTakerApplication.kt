package com.symplified.ordertaker

import android.app.Application
import android.content.Context
import com.symplified.ordertaker.data.AppDatabase
import com.symplified.ordertaker.data.repository.*

class OrderTakerApplication : Application() {

    init {
        instance = this
    }

    companion object {
        const val testStoreId = "c9315221-a003-4830-9e28-c26c3d044dff"

        private var instance: OrderTakerApplication? = null

        val database by lazy { AppDatabase.getDatabase(applicationContext()) }
        val tableRepository by lazy { TableRepository(database.tableDao()) }
        val zoneRepository by lazy { ZoneRepository(database.zoneDao()) }
        val cartItemRepository by lazy { CartItemRepository(database.cartItemDao()) }
        val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
        val menuItemRepository by lazy { MenuItemRepository(database.menuItemDao()) }

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = OrderTakerApplication.applicationContext()
    }
}
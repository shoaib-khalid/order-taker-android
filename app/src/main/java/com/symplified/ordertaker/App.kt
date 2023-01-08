package com.symplified.ordertaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.symplified.ordertaker.data.AppDatabase
import com.symplified.ordertaker.data.repository.*

class App : Application() {

    init {
        instance = this
    }

    companion object {
        const val testStoreId = "c9315221-a003-4830-9e28-c26c3d044dff"
        const val SHARED_PREFS_FILENAME = "Symplified Order Taker Shared Preferences File"
        const val IS_LOGGED_IN = "is_logged_in"

        private var instance: App? = null

        val database by lazy { AppDatabase.getDatabase(applicationContext()) }
        val tableRepository by lazy { TableRepository(database.tableDao()) }
        val zoneRepository by lazy { ZoneRepository(database.zoneDao()) }
        val cartItemRepository by lazy { CartItemRepository(database.cartItemDao()) }
        val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
        val productRepository by lazy { ProductRepository(database.productDao()) }

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun sharedPreferences(): SharedPreferences {
            return applicationContext().getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = App.applicationContext()
    }
}
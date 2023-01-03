package com.symplified.ordertaker

import android.app.Application
import android.content.Context
import com.symplified.ordertaker.data.AppDatabase
import com.symplified.ordertaker.data.repository.CartItemRepository
import com.symplified.ordertaker.data.repository.CategoryRepository

class OrderTakerApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: OrderTakerApplication? = null

        val database by lazy { AppDatabase.getDatabase(applicationContext()) }
        val cartItemRepository by lazy { CartItemRepository(database.cartItemDao()) }
        val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = OrderTakerApplication.applicationContext()
    }
}
package com.symplified.ordertaker

import android.app.Application
import android.content.Context
import com.symplified.ordertaker.data.AppDatabase
import com.symplified.ordertaker.data.repository.CartItemRepository

class OrderTakerApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: OrderTakerApplication? = null

        val database by lazy { AppDatabase.getDatabase(applicationContext()) }
        val repository by lazy { CartItemRepository(database.cartItemDao()) }

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = OrderTakerApplication.applicationContext()
    }
}
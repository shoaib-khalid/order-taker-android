package com.symplified.easydukanpos

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import com.symplified.easydukanpos.data.AppDatabase
import com.symplified.easydukanpos.data.repository.*

class App : Application() {

    init {
        instance = this
    }

    companion object {

        private const val SHARED_PREFS_FILENAME = "Symplified Order Taker Shared Preferences File"

        const val ASSET_URL_PRODUCTION = "https://assets.symplified.biz/product-assets"
        const val ASSET_URL_STAGING = "https://assets.symplified.it/product-assets"

        private var instance: App? = null

        val database by lazy { AppDatabase.getDatabase(applicationContext()) }
        val tableRepository by lazy { TableRepository(database.tableDao()) }
        val zoneRepository by lazy { ZoneRepository(database.zoneDao(), database.tableDao()) }
        val cartItemRepository by lazy {
            CartItemRepository(
                database.cartItemDao(),
                database.cartSubItemDao(),
                database.cartItemAddOnDao()
            )
        }
        val cartSubItemRepository by lazy { CartSubItemRepository(database.cartSubItemDao()) }
        val productRepository by lazy {
            ProductRepository(
                database.categoryDao(),
                database.productDao(),
                database.productInventoryDao(),
                database.productInventoryItemDao(),
                database.productVariantDao(),
                database.productVariantAvailableDao(),
                database.productAddOnGroupDao(),
                database.productAddOnItemDetailsDao(),
                database.productPackageDao(),
                database.productPackageOptionDetailsDao(),
                database.bestSellerDao()
            )
        }
        val paymentChannelRepository by lazy { PaymentChannelRepository(database.paymentChannelDao()) }
        val userRepository by lazy { UserRepository(database.userDao()) }

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun sharedPreferences(): SharedPreferences {
            return applicationContext().getSharedPreferences(
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE
            )
        }

        fun isConnectedToInternet(): Boolean {
            val cm =
                applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null
            } else {
                @Suppress("DEPRECATION")
                cm.activeNetworkInfo?.isConnected ?: false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = App.applicationContext()
    }
}
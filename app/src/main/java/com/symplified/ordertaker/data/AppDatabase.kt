package com.symplified.ordertaker.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.symplified.ordertaker.data.dao.*
import com.symplified.ordertaker.models.cartitems.CartItem
import com.symplified.ordertaker.models.cartitems.CartItemAddOn
import com.symplified.ordertaker.models.cartitems.CartSubItem
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.paymentchannel.PaymentChannel
import com.symplified.ordertaker.models.products.Product
import com.symplified.ordertaker.models.products.addons.ProductAddOnDetails
import com.symplified.ordertaker.models.products.addons.ProductAddOnGroup
import com.symplified.ordertaker.models.products.inventories.ProductInventory
import com.symplified.ordertaker.models.products.inventories.ProductInventoryItem
import com.symplified.ordertaker.models.products.options.ProductPackage
import com.symplified.ordertaker.models.products.options.ProductPackageOptionDetails
import com.symplified.ordertaker.models.products.variants.ProductVariant
import com.symplified.ordertaker.models.products.variants.ProductVariantAvailable
import com.symplified.ordertaker.models.users.User
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.Table

@Database(
    version = 1,
    entities = [
        Table::class,
        Zone::class,
        Category::class,
        CartItem::class,
        CartSubItem::class,
        CartItemAddOn::class,
        Product::class,
        ProductInventory::class,
        ProductInventoryItem::class,
        ProductVariant::class,
        ProductVariantAvailable::class,
        ProductAddOnGroup::class,
        ProductAddOnDetails::class,
        ProductPackage::class,
        ProductPackageOptionDetails::class,
        PaymentChannel::class,
        User::class
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tableDao(): TableDao
    abstract fun zoneDao(): ZoneDao
    abstract fun categoryDao(): CategoryDao

    abstract fun cartItemDao(): CartItemDao
    abstract fun cartItemAddOnDao(): CartItemAddOnDao
    abstract fun cartSubItemDao(): CartSubItemDao

    // DAOs for products and product sub-items
    abstract fun productDao(): ProductDao
    abstract fun productInventoryDao(): ProductInventoryDao
    abstract fun productInventoryItemDao(): ProductInventoryItemDao
    abstract fun productVariantDao(): ProductVariantDao
    abstract fun productVariantAvailableDao(): ProductVariantAvailableDao
    abstract fun productAddOnGroupDao(): ProductAddOnGroupDao
    abstract fun productAddOnItemDetailsDao(): ProductAddOnItemDetailsDao
    abstract fun productPackageDao(): ProductPackageDao
    abstract fun productPackageOptionDetailsDao(): ProductPackageOptionDetailsDao

    abstract fun paymentChannelDao(): PaymentChannelDao

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "order_taker_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
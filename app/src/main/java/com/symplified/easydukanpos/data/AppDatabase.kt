package com.symplified.easydukanpos.data

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.symplified.easydukanpos.data.dao.*
import com.symplified.easydukanpos.models.bestsellers.BestSeller
import com.symplified.easydukanpos.models.cartitems.CartItem
import com.symplified.easydukanpos.models.cartitems.CartItemAddOn
import com.symplified.easydukanpos.models.cartitems.CartSubItem
import com.symplified.easydukanpos.models.categories.Category
import com.symplified.easydukanpos.models.paymentchannel.PaymentChannel
import com.symplified.easydukanpos.models.products.Product
import com.symplified.easydukanpos.models.products.addons.ProductAddOnDetails
import com.symplified.easydukanpos.models.products.addons.ProductAddOnGroup
import com.symplified.easydukanpos.models.products.inventories.ProductInventory
import com.symplified.easydukanpos.models.products.inventories.ProductInventoryItem
import com.symplified.easydukanpos.models.products.options.ProductPackage
import com.symplified.easydukanpos.models.products.options.ProductPackageOptionDetails
import com.symplified.easydukanpos.models.products.variants.ProductVariant
import com.symplified.easydukanpos.models.products.variants.ProductVariantAvailable
import com.symplified.easydukanpos.models.users.User
import com.symplified.easydukanpos.models.zones.Table
import com.symplified.easydukanpos.models.zones.Zone

@Database(
    version = 3,
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
        User::class,
        BestSeller::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
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

    abstract fun bestSellerDao(): BestSellerDao

    abstract fun paymentChannelDao(): PaymentChannelDao

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val res = database.query("PRAGMA table_info(products)")
                Log.d("db-migration", "res: ${res.count}")
                val movedToFirst = res.moveToFirst()
                Log.d("db-migration", "movedToFirst: $movedToFirst")

                var isColumnExists = false
                do {
                    Log.d("db-migration", res.getString(1))
                    if (res.getString(1) == "description") {
                        isColumnExists = true
                    }
                } while (res.moveToNext())

                if (!isColumnExists) {
                    database.execSQL(
                        "ALTER TABLE products " +
                                "ADD COLUMN description TEXT DEFAULT '' NOT NULL"
                    )
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "order_taker_db"
                ).addMigrations(MIGRATION_2_3).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
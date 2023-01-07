package com.symplified.ordertaker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.symplified.ordertaker.models.CartItem
import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.Table

@Database(
    entities = [
        Table::class,
        Zone::class,
        Category::class,
//        Product::class,
        CartItem::class,
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tableDao(): TableDao
    abstract fun zoneDao(): ZoneDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun cartItemDao(): CartItemDao

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
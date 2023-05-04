package com.symplified.easydukanpos.models.cartitems

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemName: String,
    var itemPrice: Double,
    val itemCode: String,
    val productId: String,
    var quantity: Int = 1
)
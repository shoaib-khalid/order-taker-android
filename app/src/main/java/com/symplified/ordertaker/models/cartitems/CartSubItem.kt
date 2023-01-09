package com.symplified.ordertaker.models.cartitems

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_sub_items")
data class CartSubItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val SKU: String,
    val productName: String,
    val itemCode: String,
    var cartItemId: Int,
    var quantity: Int,
    val productPrice: Double,
    val specialInstruction: String,
    val productId: String
)

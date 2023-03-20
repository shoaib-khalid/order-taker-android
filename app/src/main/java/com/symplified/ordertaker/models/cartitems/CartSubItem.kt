package com.symplified.ordertaker.models.cartitems

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_sub_items")
data class CartSubItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val SKU: String = "",
    val productName: String,
    val itemCode: String,
    var cartItemId: Long = 0,
    var quantity: Int = 1,
    val productPrice: Double,
    val specialInstruction: String = "",
    val productId: String,
    val optionId: String,
    val packageGroupId: String
)

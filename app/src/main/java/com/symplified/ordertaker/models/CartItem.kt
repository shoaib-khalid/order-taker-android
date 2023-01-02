package com.symplified.ordertaker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemName: String,
    val itemPrice: Double,
    var quantity: Int
)
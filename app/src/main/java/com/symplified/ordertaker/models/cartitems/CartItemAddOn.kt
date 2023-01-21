package com.symplified.ordertaker.models.cartitems

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_item_add_ons")
data class CartItemAddOn(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productAddOnId: String,
    val name: String,
    var cartItemId: Long = 0
)

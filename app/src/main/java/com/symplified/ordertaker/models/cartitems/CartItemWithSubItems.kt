package com.symplified.ordertaker.models.cartitems

import androidx.room.Embedded
import androidx.room.Relation

data class CartItemWithSubItems(
    @Embedded val cartItem: CartItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "cartItemId"
    )
    val cartSubItems: List<CartSubItem>
)

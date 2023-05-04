package com.symplified.easydukanpos.models.cartitems

import androidx.room.Embedded
import androidx.room.Relation

data class CartItemWithAddOnsAndSubItems(
    @Embedded val cartItem: CartItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "cartItemId"
    )
    val cartItemAddons: List<CartItemAddOn>,
    @Relation(
        parentColumn = "id",
        entityColumn = "cartItemId"
    )
    val cartSubItems: List<CartSubItem>
)

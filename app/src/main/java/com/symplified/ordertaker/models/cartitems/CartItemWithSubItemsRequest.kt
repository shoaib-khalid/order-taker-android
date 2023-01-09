package com.symplified.ordertaker.models.cartitems

data class CartItemWithSubItemsRequest(
    val itemCode: String,
    val productId: String,
    val quantity: Int,
    val cartSubItem: List<CartSubItem>
)

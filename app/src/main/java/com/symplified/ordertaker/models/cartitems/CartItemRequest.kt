package com.symplified.ordertaker.models.cartitems

data class CartItemRequest(
    val itemCode: String,
    val productId: String,
    val quantity: Int,
    val specialInstruction: String = "",
    val cartItemAddOn: List<CartItemAddOn>?,
    val cartSubItem: List<CartSubItem>?
)

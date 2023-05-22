package com.symplified.ordertaker.models.cartitems

data class CartItemRequest(
    val itemCode: String,
    val productId: String,
    val quantity: Int,
    val productPrice: Double,
    val specialInstruction: String = "",
    val cartItemAddOn: List<CartItemAddOnRequest>?,
    val cartSubItem: List<CartSubItemRequest>?
)
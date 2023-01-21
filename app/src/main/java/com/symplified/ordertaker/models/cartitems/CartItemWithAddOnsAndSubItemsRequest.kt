package com.symplified.ordertaker.models.cartitems

data class CartItemWithAddOnsAndSubItemsRequest(
    val itemCode: String,
    val productId: String,
    val quantity: Int,
    val cartItemAddOn: List<CartItemAddOn>,
    val cartSubItem: List<CartSubItem>
)

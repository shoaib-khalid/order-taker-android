package com.symplified.easydukanpos.models.cartitems

data class CartSubItemRequest(
    val SKU: String,
    val productName: String,
    val productId: String,
    val itemCode: String,
    val quantity: Int = 1,
    val productPrice: Double = 0.0,
    val specialInstruction: String = ""
)

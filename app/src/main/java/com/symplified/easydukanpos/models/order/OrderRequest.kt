package com.symplified.easydukanpos.models.order

import com.symplified.easydukanpos.models.cartitems.CartItemRequest

data class OrderRequest(
    val storeId: String,
    val customerNotes: String,
    val dineInPack: String = "DINEIN",
    val dineInOption: String = "SENDTOTABLE",
    val orderPaymentDetails: OrderPaymentDetails,
    val orderShipmentDetails: OrderShipmentDetails = OrderShipmentDetails(),
    val cartItems: List<CartItemRequest>
)
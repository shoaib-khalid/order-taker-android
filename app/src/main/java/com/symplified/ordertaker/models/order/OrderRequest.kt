package com.symplified.ordertaker.models.order

import com.symplified.ordertaker.models.cartitems.CartItemRequest

data class OrderRequest(
    val storeId: String,
    val customerNotes: String,
    val dineInPack: String = "DINEIN",
    val dineInOption: String = "SENDTOTABLE",
    val orderPaymentDetails: OrderPaymentDetails,
    val orderShipmentDetails: OrderShipmentDetails = OrderShipmentDetails(),
    val cartItems: List<CartItemRequest>
)
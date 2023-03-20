package com.symplified.ordertaker.models.order

import com.symplified.ordertaker.models.cartitems.CartItemRequest

data class OrderRequest(
    val cartItems: List<CartItemRequest>,
    val customerId: String?,
    val storeId: String,
    val customerNotes: String,
    val dineInPack: String,
    val dineInOption: String,
    val orderPaymentDetails: OrderPaymentDetails,
    val orderShipmentDetails: OrderShipmentDetails
) {
    constructor(
        cartItems: List<CartItemRequest>,
        storeId: String,
        orderPaymentDetails: OrderPaymentDetails,
        customerNotes: String
    ) : this(
        cartItems,
        null,
        storeId,
        customerNotes,
        "DINEIN",
        "SENDTOTABLE",
        orderPaymentDetails,
        OrderShipmentDetails(
            "", "", "", "", true, "", ""
        )
    )
}

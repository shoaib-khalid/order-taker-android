package com.symplified.ordertaker.models.cartitems

data class OrderRequest(
    val cartItems: List<CartItemWithSubItemsRequest>,
    val customerId: String?,
    val storeId: String,
    val customerNotes: String,
    val dineInPack: String,
    val dineInOption: String,
    val orderPaymentDetails: OrderPaymentDetails,
    val orderShipmentDetails: OrderShipmentDetails
) {
    constructor(
        cartItems: List<CartItemWithSubItemsRequest>,
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

package com.symplified.ordertaker.models.cartitems

data class OrderRequest(
    val cartItems: List<CartItemWithSubItemsRequest>,
    val customerId: String?,
    val storeId: String,
    val customerNotes: String,
    val dineInPack: String,
    val orderPaymentDetails: OrderPaymentDetails,
    val orderShipmentDetails: OrderShipmentDetails
) {
    constructor(
        cartItems: List<CartItemWithSubItemsRequest>,
        storeId: String,
        orderPaymentDetails: OrderPaymentDetails,
    ) : this(
        cartItems, null, storeId, "Self Collect", "DINEIN", orderPaymentDetails,
        OrderShipmentDetails(
            "", "", "", "", true, "", ""
        )
    )
}

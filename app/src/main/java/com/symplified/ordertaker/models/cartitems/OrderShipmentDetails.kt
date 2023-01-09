package com.symplified.ordertaker.models.cartitems

data class OrderShipmentDetails(
    val address: String,
    val city: String,
    val zipcode: String,
    val state: String,
    val storePickup: Boolean,
    val email: String,
    val phoneNumber: String
)

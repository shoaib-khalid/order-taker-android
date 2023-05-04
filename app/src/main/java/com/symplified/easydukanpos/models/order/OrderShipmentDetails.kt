package com.symplified.easydukanpos.models.order

data class OrderShipmentDetails(
    val address: String = "",
    val city: String = "",
    val zipcode: String = "",
    val state: String = "",
    val storePickup: Boolean = true,
    val email: String = "",
    val phoneNumber: String = ""
)

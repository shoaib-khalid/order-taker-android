package com.symplified.ordertaker.models.order

data class OrderResponseData(
    val serviceCharges: Double,
    val appliedDiscount: Double,
    val subTotal: Double,
    val total: Double,
    val orderTimeConverted: String
)

package com.symplified.easydukanpos.models.order

data class OrderResponseData(
    val id: String,
    val serviceCharges: Double,
    val appliedDiscount: Double,
    val subTotal: Double,
    val total: Double,
    val orderTimeConverted: String,
    val orderList: List<OrderResponseData>
)

package com.symplified.ordertaker.models.paymentchannel

enum class PaymentOption(
    val displayName: String,
    val endpoint: String,
    var isEnabled: Boolean = true
) {
    CASH("Cash", ""),
    CARD("Credit/Debit Card", "online-payment"),
    PAYLATER("Pay Later", "pay-later")
}
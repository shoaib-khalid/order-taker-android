package com.symplified.ordertaker.models

data class CartItem(
    val item: Item,
    var quantity: Int
)
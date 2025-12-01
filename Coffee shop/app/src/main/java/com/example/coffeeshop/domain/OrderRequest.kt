package com.example.coffeeshop.domain

import java.math.BigDecimal

data class OrderRequest(
    val deliveryAddress: String,
    val deliveryFee: BigDecimal = BigDecimal.ZERO,
    val items: List<OrderCartItem>
)

data class OrderCartItem(
    val coffeeId: Int,
    val selectedSize: String
)
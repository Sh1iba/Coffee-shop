package com.example.coffeeshop.domain

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class OrderRequest(
    val deliveryAddress: String,
    val deliveryFee: BigDecimal = BigDecimal.ZERO,
    val items: List<OrderCartItem>
)

data class OrderCartItem(
    @SerializedName("productId") val coffeeId: Int,
    val selectedSize: String
)

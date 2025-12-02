package com.example.coffeeshop.data.remote.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val totalAmount: BigDecimal,
    val deliveryFee: BigDecimal,
    val deliveryAddress: String,
    val orderDate: String,
    val items: List<OrderItemResponse>
)

data class OrderItemResponse(
    val id: Long,
    val coffeeName: String,
    val selectedSize: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
    val totalPrice: BigDecimal
)

data class OrdersHistoryResponse(
    val orders: List<OrderResponse>
)
package com.example.coffeeshop.data.remote.response

data class CoffeeCartResponse(
    val id: Int,
    val name: String,
    val selectedSize: String,
    val price: Float,
    val quantity: Int,
    val totalPrice: Float,
    val imageName: String
)

data class CartSummaryResponse(
    val items: List<CoffeeCartResponse>,
    val totalItems: Int,
    val totalPrice: Float
)


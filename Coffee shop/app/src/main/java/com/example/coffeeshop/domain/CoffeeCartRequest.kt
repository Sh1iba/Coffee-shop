package com.example.coffeeshop.domain

data class CoffeeCartRequest(
    val coffeeId: Int,
    val selectedSize: String,
    val quantity: Int = 1
)

data class UpdateCartQuantityRequest(
    val quantity: Int
)
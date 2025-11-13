package com.example.coffeeshop.domain

data class FavoriteCoffeeRequest(
    val coffeeId: Int,
    val selectedSize: String
)
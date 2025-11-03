package com.example.coffeeshop.data.remote.response

data class CoffeeResponse(
    val id: Int,
    val type: CoffeeTypeResponse,
    val name: String,
    val description: String,
    val price: Float,
    val imageName: String
)
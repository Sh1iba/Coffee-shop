package com.example.coffeeshop.data.remote.response

data class CoffeeResponse(
    val id: Int,
    val type: CoffeeTypeResponse,
    val name: String,
    val description: String,
    val sizes: List<CoffeeSizeResponse>,
    val imageName: String
)

data class CoffeeSizeResponse(
    val size: String,
    val price: Float
)
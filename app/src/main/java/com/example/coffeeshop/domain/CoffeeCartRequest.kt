package com.example.coffeeshop.domain

import com.google.gson.annotations.SerializedName

data class CartItemRequest(
    @SerializedName("productId") val productId: Int,
    val selectedSize: String,
    val quantity: Int = 1
)

data class UpdateCartQuantityRequest(
    val quantity: Int
)

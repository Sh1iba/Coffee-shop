package com.example.coffeeshop.domain

import com.google.gson.annotations.SerializedName

data class FavoriteProductRequest(
    @SerializedName("productId") val productId: Int,
    val selectedSize: String
)

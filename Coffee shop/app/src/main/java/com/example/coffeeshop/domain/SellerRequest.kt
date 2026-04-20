package com.example.coffeeshop.domain

data class SellerRequest(
    val name: String,
    val description: String,
    val category: String,
    val logoImage: String? = null
)

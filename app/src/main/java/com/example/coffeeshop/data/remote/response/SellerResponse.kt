package com.example.coffeeshop.data.remote.response

data class SellerResponse(
    val id: Long,
    val name: String,
    val description: String,
    val category: String,
    val logoImage: String?,
    val rating: Double,
    val isActive: Boolean,
    val ownerId: Long,
    val ownerName: String
)

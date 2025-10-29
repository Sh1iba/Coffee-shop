package com.example.coffeeshop.network.model.response

data class LoginResponse(
    val userId: Long,
    val token: String,
    val email: String,
    val name: String
)
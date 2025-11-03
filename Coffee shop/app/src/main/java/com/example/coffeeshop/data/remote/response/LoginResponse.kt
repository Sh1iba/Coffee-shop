package com.example.coffeeshop.data.remote.response

data class LoginResponse(
    val userId: Long,
    val token: String,
    val email: String,
    val name: String
)
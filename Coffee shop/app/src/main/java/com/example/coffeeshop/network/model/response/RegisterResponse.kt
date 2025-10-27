package com.example.coffeeshop.network.model.response

data class RegisterResponse(
    val userID: Long,
    val email: String,
    val name: String
)
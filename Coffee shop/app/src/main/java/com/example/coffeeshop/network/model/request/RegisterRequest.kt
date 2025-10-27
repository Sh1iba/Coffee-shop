package com.example.coffeeshop.network.model.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
)
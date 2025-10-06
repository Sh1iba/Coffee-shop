package com.example.coffeeshop.network.model.register

data class RegisterRequest(
    val login: String,
    val email: String,
    val password: String
)
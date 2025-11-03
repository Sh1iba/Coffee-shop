package com.example.coffeeshop.domain

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
)
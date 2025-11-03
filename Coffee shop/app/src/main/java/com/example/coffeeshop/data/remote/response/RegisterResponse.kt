package com.example.coffeeshop.data.remote.response

data class RegisterResponse(
    val userID: Long,
    val email: String,
    val name: String
)
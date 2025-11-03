package com.example.coffeeshop.data.remote.response

data class ErrorResponse(
    val success: Boolean,
    val message: String,
    val errorCode: String
)
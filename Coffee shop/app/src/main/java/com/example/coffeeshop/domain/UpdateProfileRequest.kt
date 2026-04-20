package com.example.coffeeshop.domain

data class UpdateProfileRequest(
    val name: String? = null,
    val currentPassword: String? = null,
    val newPassword: String? = null
)

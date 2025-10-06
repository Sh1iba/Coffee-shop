package com.example.coffeeshop.network.api

import com.example.coffeeshop.network.model.login.LoginRequest
import com.example.coffeeshop.network.model.login.LoginResponse
import com.example.coffeeshop.network.model.register.RegisterRequest
import com.example.coffeeshop.network.model.register.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

}
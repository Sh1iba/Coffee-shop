package com.example.coffeeshop.network.api

import com.example.coffeeshop.network.model.request.LoginRequest
import com.example.coffeeshop.network.model.response.LoginResponse
import com.example.coffeeshop.network.model.request.RegisterRequest
import com.example.coffeeshop.network.model.response.CoffeeResponse
import com.example.coffeeshop.network.model.response.CoffeeTypeResponse
import com.example.coffeeshop.network.model.response.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @GET("coffee/types")
    suspend fun getAllCoffeeTypes(@Header("Authorization") token: String? = null):
            Response<List<CoffeeTypeResponse>>

    @GET("coffee")
    suspend fun getAllCoffee(@Header("Authorization") token: String? = null):
            Response<List<CoffeeResponse>>

    @GET("coffee/image/{imageName}")
    suspend fun getCoffeeImage(
        @Path("imageName") imageName: String,
        @Header("Authorization") token: String? = null
    ): Response<ResponseBody>
}
package com.example.coffeeshop.data.remote.api

import com.example.coffeeshop.data.remote.response.ApiResponse
import com.example.coffeeshop.domain.LoginRequest
import com.example.coffeeshop.domain.RegisterRequest
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.data.remote.response.FavoriteCoffeeResponse
import com.example.coffeeshop.data.remote.response.LoginResponse
import com.example.coffeeshop.data.remote.response.RegisterResponse
import com.example.coffeeshop.domain.FavoriteCoffeeRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @GET("coffee/favorites")
    suspend fun getFavorites(@Header("Authorization") token: String? = null):
            Response<List<FavoriteCoffeeResponse>>

    @POST("coffee/favorites")
    suspend fun addToFavorites(
        @Header("Authorization") token: String? = null,
        @Body request: FavoriteCoffeeRequest
    ): Response<ApiResponse>

    @DELETE("coffee/favorites/{coffeeId}")
    suspend fun removeFromFavorites(
        @Header("Authorization") token: String? = null,
        @Path("coffeeId") coffeeId: Int
    ): Response<ApiResponse>

}

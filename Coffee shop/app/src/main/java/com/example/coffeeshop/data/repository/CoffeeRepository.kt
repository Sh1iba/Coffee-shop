package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse

class CoffeeRepository(
    private val apiService: ApiService
) {

    suspend fun getAllCoffee(token: String): List<CoffeeResponse> {
        val response = apiService.getAllCoffee(token)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun getAllCoffeeTypes(token: String): List<CoffeeTypeResponse> {
        val response = apiService.getAllCoffeeTypes(token)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun getCoffeeImage(imageName: String, token: String): ByteArray? {
        return try {
            val response = apiService.getCoffeeImage(imageName, token)
            if (response.isSuccessful) {
                response.body()?.bytes()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
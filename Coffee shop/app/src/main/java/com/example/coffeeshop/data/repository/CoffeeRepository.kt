package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.data.remote.response.FavoriteCoffeeResponse
import com.example.coffeeshop.domain.FavoriteCoffeeRequest

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

    suspend fun getFavorites(token: String): List<FavoriteCoffeeResponse> {
        val response = apiService.getFavorites(token)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun addToFavorites(token: String, coffeeId: Int): Boolean {
        return try {
            val request = FavoriteCoffeeRequest(coffeeId = coffeeId)
            val response = apiService.addToFavorites(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromFavorites(token: String, coffeeId: Int): Boolean {
        return try {
            val response = apiService.removeFromFavorites(token, coffeeId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
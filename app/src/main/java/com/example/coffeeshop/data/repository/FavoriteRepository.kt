package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.data.remote.response.FavoriteProductResponse
import com.example.coffeeshop.domain.FavoriteProductRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getFavorites(): List<FavoriteProductResponse> {
        return try {
            val response = apiService.getFavorites()
            if (response.isSuccessful) response.body() ?: emptyList()
            else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToFavorites(productId: Int, selectedSize: String): Boolean {
        return try {
            apiService.addToFavorites(FavoriteProductRequest(productId, selectedSize)).isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromFavorites(productId: Int, size: String? = null): Boolean {
        return try {
            apiService.removeFromFavorites(productId, size).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

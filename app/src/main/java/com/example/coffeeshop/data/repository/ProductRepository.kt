package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.data.remote.response.ProductCategoryResponse
import com.example.coffeeshop.data.remote.response.ProductResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllProducts(): List<ProductResponse> {
        val response = apiService.getAllCoffee(page = 0, size = 100)
        if (!response.isSuccessful) throw Exception("HTTP ${response.code()}: ${response.message()}")
        return response.body()?.content ?: emptyList()
    }

    suspend fun getProductById(productId: Int): ProductResponse? {
        return try {
            getAllProducts().find { it.id == productId }
        } catch (e: Exception) { null }
    }

    suspend fun getAllCategories(): List<ProductCategoryResponse> {
        val response = apiService.getAllCoffeeTypes()
        if (!response.isSuccessful) throw Exception("HTTP ${response.code()}: ${response.message()}")
        return response.body() ?: emptyList()
    }

    suspend fun getPopularProducts(limit: Int = 8): List<ProductResponse> {
        return try {
            val response = apiService.getPopularProducts(limit)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getRecommendedProducts(limit: Int = 8): List<ProductResponse> {
        return try {
            val response = apiService.getRecommendedProducts(limit)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun logProductView(productId: Int) {
        try { apiService.logProductView(productId) } catch (_: Exception) {}
    }
}

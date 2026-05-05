package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.data.remote.response.CartSummaryResponse
import com.example.coffeeshop.domain.CartItemRequest
import com.example.coffeeshop.domain.UpdateCartQuantityRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCart(): CartSummaryResponse {
        return try {
            val response = apiService.getCart()
            if (response.isSuccessful) response.body() ?: CartSummaryResponse(emptyList(), 0, 0f)
            else CartSummaryResponse(emptyList(), 0, 0f)
        } catch (e: Exception) {
            CartSummaryResponse(emptyList(), 0, 0f)
        }
    }

    suspend fun addToCart(productId: Int, selectedSize: String, quantity: Int = 1): Boolean {
        return try {
            apiService.addToCart(CartItemRequest(productId, selectedSize, quantity)).isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateQuantity(productId: Int, selectedSize: String, quantity: Int): Boolean {
        return try {
            apiService.updateCartQuantity(productId, selectedSize, UpdateCartQuantityRequest(quantity)).isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromCart(productId: Int, selectedSize: String): Boolean {
        return try {
            apiService.removeFromCart(productId, selectedSize).isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearCart(): Boolean {
        return try {
            apiService.clearCart().isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isInCart(productId: Int, selectedSize: String): Boolean {
        return try {
            getCart().items.any { it.id == productId && it.selectedSize == selectedSize }
        } catch (e: Exception) {
            false
        }
    }
}

package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.data.remote.response.CartSummaryResponse
import com.example.coffeeshop.data.remote.response.CoffeeCartResponse
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.data.remote.response.FavoriteCoffeeResponse
import com.example.coffeeshop.data.remote.response.OrderResponse
import com.example.coffeeshop.domain.CoffeeCartRequest
import com.example.coffeeshop.domain.FavoriteCoffeeRequest
import com.example.coffeeshop.domain.OrderCartItem
import com.example.coffeeshop.domain.OrderRequest
import com.example.coffeeshop.domain.UpdateCartQuantityRequest
import java.math.BigDecimal

class CoffeeRepository(
    private val apiService: ApiService
) {
    suspend fun getCoffeeById(coffeeId: Int, token: String): CoffeeResponse? {
        return try {
            val allCoffee = getAllCoffee(token)
            allCoffee.find { it.id == coffeeId }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

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

    suspend fun addToFavorites(token: String, coffeeId: Int, selectedSize: String): Boolean {
        return try {
            val request = FavoriteCoffeeRequest(
                coffeeId = coffeeId,
                selectedSize = selectedSize
            )
            val response = apiService.addToFavorites(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromFavorites(token: String, coffeeId: Int, size: String? = null): Boolean {
        return try {
            val response = apiService.removeFromFavorites(token, coffeeId, size)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCart(token: String): CartSummaryResponse {
        val response = apiService.getCart(token)
        return if (response.isSuccessful) {
            response.body() ?: CartSummaryResponse(emptyList(), 0, 0f)
        } else {
            CartSummaryResponse(emptyList(), 0, 0f)
        }
    }

    suspend fun addToCart(token: String, request: CoffeeCartRequest): Boolean {
        return try {
            val response = apiService.addToCart(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateCartQuantity(
        token: String,
        coffeeId: Int,
        selectedSize: String,
        request: UpdateCartQuantityRequest
    ): Boolean {
        return try {
            val response = apiService.updateCartQuantity(token, coffeeId, selectedSize, request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromCart(token: String, coffeeId: Int, selectedSize: String): Boolean {
        return try {
            val response = apiService.removeFromCart(token, coffeeId, selectedSize)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearCart(token: String): Boolean {
        return try {
            val response = apiService.clearCart(token)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createOrder(
        token: String,
        items: List<CoffeeCartResponse>,
        address: String,
        deliveryFee: Double
    ): Boolean {
        return try {
            val orderItems = items.map { cartItem ->
                OrderCartItem(
                    coffeeId = cartItem.id,
                    selectedSize = cartItem.selectedSize
                )
            }

            val request = OrderRequest(
                deliveryAddress = address,
                deliveryFee = BigDecimal.valueOf(deliveryFee),
                items = orderItems
            )

            val response = apiService.createOrder(token, request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getOrderHistory(token: String): List<OrderResponse> {
        val response = apiService.getOrderHistory(token)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }
}
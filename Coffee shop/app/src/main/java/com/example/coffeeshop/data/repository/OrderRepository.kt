package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.data.remote.response.CartItemResponse
import com.example.coffeeshop.data.remote.response.OrderResponse
import com.example.coffeeshop.domain.OrderCartItem
import com.example.coffeeshop.domain.OrderRequest
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun createOrder(
        items: List<CartItemResponse>,
        address: String,
        deliveryFee: Double
    ): Boolean {
        return try {
            val request = OrderRequest(
                deliveryAddress = address,
                deliveryFee = BigDecimal.valueOf(deliveryFee),
                items = items.map { OrderCartItem(coffeeId = it.id, selectedSize = it.selectedSize) }
            )
            apiService.createOrder(request).isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getOrderHistory(): List<OrderResponse> {
        return try {
            val response = apiService.getOrderHistory()
            if (response.isSuccessful) response.body() ?: emptyList()
            else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun cancelOrder(orderId: Long): Boolean {
        return try {
            apiService.cancelOrder(orderId).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

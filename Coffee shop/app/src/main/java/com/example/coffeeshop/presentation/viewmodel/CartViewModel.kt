package com.example.coffeeshop.presentation.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.response.CartSummaryResponse
import com.example.coffeeshop.data.remote.response.CoffeeCartResponse
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import com.example.coffeeshop.domain.CoffeeCartRequest
import com.example.coffeeshop.domain.UpdateCartQuantityRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder


class CartViewModel(
    internal val repository: CoffeeRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _cartSummary = MutableStateFlow<CartSummaryResponse?>(null)
    val cartSummary: StateFlow<CartSummaryResponse?> = _cartSummary.asStateFlow()

    val cartItems: StateFlow<List<CoffeeCartResponse>> = _cartSummary.map { summary ->
        summary?.items ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _imageMap = mutableStateMapOf<Int, ByteArray?>()

    val totalPrice: StateFlow<Double> = _cartSummary.map { summary ->
        summary?.totalPrice?.toDouble() ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val totalItems: StateFlow<Int> = _cartSummary.map { summary ->
        summary?.totalItems ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)


    suspend fun checkIfInCart(coffeeId: Int): String? {
        return try {
            val token = prefsManager.getToken()
            if (token != null) {
                val cart = repository.getCart(token)
                val cartItem = cart.items.find { it.id == coffeeId }
                cartItem?.selectedSize
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun loadCart() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val cartSummary = repository.getCart(token)
                    _cartSummary.value = cartSummary

                    cartSummary.items.forEach { item ->
                        loadCoffeeImage(item.id, item.imageName, token)
                    }
                } else {
                    _error.value = "Пользователь не авторизован"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки корзины: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getFullCoffeeData(coffeeId: Int): CoffeeResponse? {
        return try {
            val token = prefsManager.getToken()
            if (token != null) {
                repository.getCoffeeById(coffeeId, token)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun addToCart(coffeeId: Int, selectedSize: String, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val request = CoffeeCartRequest(
                        coffeeId = coffeeId,
                        selectedSize = selectedSize,
                        quantity = quantity
                    )
                    val success = repository.addToCart(token, request)
                    if (success) {
                        loadCart()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка добавления в корзину: ${e.message}"
            }
        }
    }

    fun updateQuantity(coffeeId: Int, selectedSize: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val request = UpdateCartQuantityRequest(quantity = newQuantity)
                    val success = repository.updateCartQuantity(token, coffeeId, selectedSize, request)
                    if (success) {
                        loadCart()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка обновления количества: ${e.message}"
            }
        }
    }

    fun removeFromCart(coffeeId: Int, selectedSize: String) {
        viewModelScope.launch {
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val success = repository.removeFromCart(token, coffeeId, selectedSize)
                    if (success) {
                        loadCart()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка удаления из корзины: ${e.message}"
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val success = repository.clearCart(token)
                    if (success) {
                        _cartSummary.value = CartSummaryResponse(emptyList(), 0, 0f)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка очистки корзины: ${e.message}"
            }
        }
    }

    private fun loadCoffeeImage(coffeeId: Int, imageName: String, token: String) {
        viewModelScope.launch {
            try {
                if (imageName.isNotEmpty()) {
                    val bytes = repository.getCoffeeImage(imageName, token)
                    _imageMap[coffeeId] = bytes
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    suspend fun getSelectedSizeForCartItem(coffeeId: Int, selectedSize: String): String? {
        return try {
            val token = prefsManager.getToken()
            if (token != null) {
                val cart = repository.getCart(token)
                val cartItem = cart.items.find { it.id == coffeeId && it.selectedSize == selectedSize }
                cartItem?.selectedSize
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getImageForCoffee(coffeeId: Int): ByteArray? {
        return _imageMap[coffeeId]
    }

    fun clearError() {
        _error.value = null
    }
}
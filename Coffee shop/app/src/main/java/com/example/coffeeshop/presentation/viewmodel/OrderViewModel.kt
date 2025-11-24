// OrderViewModel.kt (обновленный)
package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.response.CoffeeCartResponse
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderItem(
    val cartItem: CoffeeCartResponse,
    val coffeeData: CoffeeResponse?,
    val imageBytes: ByteArray?
)

class OrderViewModel(
    private val repository: CoffeeRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItems: StateFlow<List<OrderItem>> = _orderItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadOrderItems(cartItems: List<CoffeeCartResponse>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    // Загружаем полные данные для каждого товара в корзине
                    val orderItemsList = mutableListOf<OrderItem>()

                    for (cartItem in cartItems) {
                        // Получаем полные данные кофе
                        val coffeeData = repository.getCoffeeById(cartItem.id, token)
                        // Загружаем изображение
                        val imageBytes = if (cartItem.imageName.isNotEmpty()) {
                            repository.getCoffeeImage(cartItem.imageName, token)
                        } else {
                            null
                        }

                        orderItemsList.add(
                            OrderItem(
                                cartItem = cartItem,
                                coffeeData = coffeeData,
                                imageBytes = imageBytes
                            )
                        )
                    }

                    _orderItems.value = orderItemsList
                } else {
                    _error.value = "Пользователь не авторизован"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных заказа: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
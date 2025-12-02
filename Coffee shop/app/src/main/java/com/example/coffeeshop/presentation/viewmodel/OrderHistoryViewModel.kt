package com.example.coffeeshop.presentation.screens.orderhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.response.OrderResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderHistoryViewModel(
    private val repository: CoffeeRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadOrderHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val token = prefsManager.getToken()
                if (token == null) {
                    _error.value = "Необходима авторизация"
                    return@launch
                }

                val history = repository.getOrderHistory(token)
                _orders.value = history
            } catch (e: Exception) {
                _error.value = e.message ?: "Неизвестная ошибка"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

class OrderHistoryViewModelFactory(
    private val repository: CoffeeRepository,
    private val prefsManager: PrefsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderHistoryViewModel(repository, prefsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
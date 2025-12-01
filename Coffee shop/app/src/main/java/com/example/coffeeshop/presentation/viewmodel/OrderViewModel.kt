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

// OrderViewModel.kt
data class OrderItem(
    val cartItem: CoffeeCartResponse,
    val coffeeData: CoffeeResponse?,
    val imageBytes: ByteArray?
)

data class ParsedAddress(
    val mainAddress: String,
    val addressDetails: String
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

    private val _addressNote = MutableStateFlow("")
    val addressNote: StateFlow<String> = _addressNote.asStateFlow()

    private val _showNoteDialog = MutableStateFlow(false)
    val showNoteDialog: StateFlow<Boolean> = _showNoteDialog.asStateFlow()

    private val _navigateToActiveOrder = MutableStateFlow(false)
    val navigateToActiveOrder: StateFlow<Boolean> = _navigateToActiveOrder.asStateFlow()

    private val _navigateToPickupReady = MutableStateFlow(false)
    val navigateToPickupReady: StateFlow<Boolean> = _navigateToPickupReady.asStateFlow()


    fun parseAddress(fullAddress: String): ParsedAddress {
        if (fullAddress.isEmpty()) return ParsedAddress("", "")

        return try {
            val parts = fullAddress.split(",")
            if (parts.size >= 2) {
                ParsedAddress(
                    mainAddress = parts[0].trim(),
                    addressDetails = parts.subList(1, parts.size).joinToString(", ").trim()
                )
            } else {
                ParsedAddress(mainAddress = fullAddress, addressDetails = "")
            }
        } catch (e: Exception) {
            ParsedAddress(mainAddress = fullAddress, addressDetails = "")
        }
    }

    fun loadAddressNote(address: String) {
        _addressNote.value = prefsManager.getAddressNote(address)
    }

    fun saveAddressNote(address: String, note: String) {
        _addressNote.value = note
        if (address.isNotEmpty()) {
            prefsManager.saveAddressNote(address, note)
        }
    }

    fun clearAddressNote(address: String) {
        _addressNote.value = ""
        if (address.isNotEmpty()) {
            prefsManager.clearAddressNote(address)
        }
    }

    fun showNoteDialog() {
        _showNoteDialog.value = true
    }

    fun hideNoteDialog() {
        _showNoteDialog.value = false
    }

    fun createOrder(
        items: List<CoffeeCartResponse>,
        address: String,
        note: String,
        totalPrice: Double,
        deliveryFee: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val fullAddress = if (note.isNotEmpty()) "$address ($note)" else address

                    val success = repository.createOrder(
                        token = token,
                        items = items,
                        address = fullAddress,
                        deliveryFee = deliveryFee
                    )

                    if (success) {
                        // В зависимости от типа заказа переходим на разные экраны
                        if (deliveryFee > 0.0) {
                            // ДОСТАВКА - показываем экран с курьером
                            prefsManager.saveLong("order_start_ts", System.currentTimeMillis())
                            _navigateToActiveOrder.value = true
                        } else {
                            // САМОВЫВОЗ - показываем экран "Заказ готов"
                            _navigateToPickupReady.value = true
                        }

                        println("✅ Заказ успешно создан!")
                        clearAddressNote(address)
                    } else {
                        _error.value = "Ошибка при создании заказа"
                    }
                } else {
                    _error.value = "Пользователь не авторизован"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка создания заказа: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetAllNavigation() {
        _navigateToActiveOrder.value = false
        _navigateToPickupReady.value = false
    }

    fun loadOrderItems(cartItems: List<CoffeeCartResponse>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val orderItemsList = mutableListOf<OrderItem>()

                    for (cartItem in cartItems) {
                        val coffeeData = repository.getCoffeeById(cartItem.id, token)
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
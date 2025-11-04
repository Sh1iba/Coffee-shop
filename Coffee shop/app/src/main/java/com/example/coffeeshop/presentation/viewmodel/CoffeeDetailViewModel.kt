package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class CoffeeDetailViewModel(
    private val repository: CoffeeRepository
) : ViewModel() {

    private val _coffee = MutableStateFlow<CoffeeResponse?>(null)
    val coffee: StateFlow<CoffeeResponse?> = _coffee.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes: StateFlow<ByteArray?> = _imageBytes.asStateFlow()

    private val _selectedSize = MutableStateFlow("M")
    val selectedSize: StateFlow<String> = _selectedSize.asStateFlow()

    val currentPrice: StateFlow<String> = combine(
        coffee,
        selectedSize
    ) { coffee, size ->
        if (coffee == null) return@combine "₽0.00"

        val basePrice = coffee.price
        val finalPrice = when (size) {
            "S" -> basePrice * 0.8f
            "M" -> basePrice
            "L" -> basePrice * 1.2f
            else -> basePrice
        }
        "₽${"%.2f".format(finalPrice)}"
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "₽0.00"
    )

    fun setCoffee(coffee: CoffeeResponse) {
        _coffee.value = coffee
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun selectSize(size: String) {
        _selectedSize.value = size
    }

    fun loadCoffeeImage(token: String) {
        viewModelScope.launch {
            try {
                val currentCoffee = _coffee.value
                if (currentCoffee != null) {
                    val bytes = repository.getCoffeeImage(currentCoffee.imageName, token)
                    _imageBytes.value = bytes
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
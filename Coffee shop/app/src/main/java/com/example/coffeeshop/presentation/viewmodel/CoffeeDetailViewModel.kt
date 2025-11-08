package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
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
    internal val repository: CoffeeRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _coffee = MutableStateFlow<CoffeeResponse?>(null)
    val coffee: StateFlow<CoffeeResponse?> = _coffee.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes: StateFlow<ByteArray?> = _imageBytes.asStateFlow()

    private val _selectedSize = MutableStateFlow("M")
    val selectedSize: StateFlow<String> = _selectedSize.asStateFlow()

    internal val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
        "$0.00"
    )

    fun setCoffee(coffee: CoffeeResponse) {
        _coffee.value = coffee
        checkIfFavorite(coffee.id)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentCoffee = _coffee.value ?: return@launch
            val token = prefsManager.getToken() ?: return@launch

            _isLoading.value = true
            try {
                if (_isFavorite.value) {
                    val success = repository.removeFromFavorites(token, currentCoffee.id)
                    if (success) {
                        _isFavorite.value = false
                    }
                } else {
                    val success = repository.addToFavorites(token, currentCoffee.id)
                    if (success) {
                        _isFavorite.value = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkIfFavorite(coffeeId: Int) {
        viewModelScope.launch {
            val token = prefsManager.getToken() ?: return@launch
            try {
                val favorites = repository.getFavorites(token)
                val isFavorite = favorites.any { it.id == coffeeId }
                _isFavorite.value = isFavorite
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectSize(size: String) {
        _selectedSize.value = size
    }

    fun loadCoffeeImage() {
        viewModelScope.launch {
            try {
                val currentCoffee = _coffee.value
                val token = prefsManager.getToken() ?: return@launch
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
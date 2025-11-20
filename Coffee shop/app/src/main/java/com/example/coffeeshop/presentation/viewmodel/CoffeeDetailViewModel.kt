package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeSizeResponse
import com.example.coffeeshop.data.remote.response.FavoriteCoffeeResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class CoffeeDetailViewModel(
    internal val repository: CoffeeRepository,
    private val prefsManager: PrefsManager,
    private val cartViewModel: CartViewModel
) : ViewModel() {

    private val _coffee = MutableStateFlow<CoffeeResponse?>(null)
    val coffee: StateFlow<CoffeeResponse?> = _coffee.asStateFlow()

    private val _favorites = MutableStateFlow<List<FavoriteCoffeeResponse>>(emptyList())
    val favorites: StateFlow<List<FavoriteCoffeeResponse>> = _favorites.asStateFlow()

    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes: StateFlow<ByteArray?> = _imageBytes.asStateFlow()

    private val _selectedSize = MutableStateFlow<String?>(null)
    val selectedSize: StateFlow<String?> = _selectedSize.asStateFlow()

    private val _isInCartWithCurrentSize = MutableStateFlow(false)
    val isInCartWithCurrentSize: StateFlow<Boolean> = _isInCartWithCurrentSize.asStateFlow()

    internal val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isFavoriteWithCurrentSize: StateFlow<Boolean> = combine(
        _coffee,
        _selectedSize,
        _favorites
    ) { coffee, selectedSize, favorites ->
        if (coffee == null || selectedSize == null) false
        else favorites.any { it.id == coffee.id && it.selectedSize == selectedSize }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )



    val availableSizes: StateFlow<List<CoffeeSizeResponse>> = _coffee
        .asStateFlow()
        .map { coffee ->
            coffee?.sizes ?: emptyList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val currentPrice: StateFlow<String> = combine(
        _coffee,
        _selectedSize
    ) { coffee, selectedSize ->
        if (coffee == null) return@combine "₽0.00"

        val sizeToUse = selectedSize ?: coffee.sizes.firstOrNull()?.size ?: "M"

        val sizePrice = coffee.sizes.find { it.size == sizeToUse }?.price ?: 0f
        "₽${"%.2f".format(sizePrice)}"
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "₽0.00"
    )

    fun checkIfInCartWithCurrentSize() {
        viewModelScope.launch {
            val currentCoffee = _coffee.value
            val currentSize = _selectedSize.value
            if (currentCoffee != null && currentSize != null) {
                val isInCart = cartViewModel.checkIfInCart(currentCoffee.id, currentSize)
                _isInCartWithCurrentSize.value = isInCart
            } else {
                _isInCartWithCurrentSize.value = false
            }
        }
    }

    fun setCoffee(coffee: CoffeeResponse, favoriteSize: String = "") {
        _coffee.value = coffee

        val initialSize = if (favoriteSize.isNotEmpty()) {
            favoriteSize
        } else {
            coffee.sizes.find { it.size == "M" }?.size ?: coffee.sizes.firstOrNull()?.size
        }

        _selectedSize.value = initialSize

        loadFavorites()
        checkIfInCartWithCurrentSize()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val token = prefsManager.getToken() ?: return@launch
            try {
                val favorites = repository.getFavorites(token)
                _favorites.value = favorites
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentCoffee = _coffee.value ?: return@launch
            val token = prefsManager.getToken() ?: return@launch
            val currentSelectedSize = _selectedSize.value ?: return@launch

            _isLoading.value = true
            try {
                val isCurrentlyFavorite = isFavoriteWithCurrentSize.value

                if (isCurrentlyFavorite) {
                    val success = repository.removeFromFavorites(token, currentCoffee.id, currentSelectedSize)
                    if (success) {
                        loadFavorites()
                    }
                } else {
                    val success = repository.addToFavorites(token, currentCoffee.id, currentSelectedSize)
                    if (success) {
                        loadFavorites()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
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

    fun addToCart() {
        viewModelScope.launch {
            val currentCoffee = _coffee.value ?: return@launch
            val currentSize = _selectedSize.value ?: return@launch

            _isLoading.value = true
            try {
                cartViewModel.addToCart(currentCoffee.id, currentSize, 1)
                _isInCartWithCurrentSize.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPriceForSize(size: String): Float {
        return _coffee.value?.sizes?.find { it.size == size }?.price ?: 0f
    }
}
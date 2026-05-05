package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.remote.response.FavoriteProductResponse
import com.example.coffeeshop.data.remote.response.ProductResponse
import com.example.coffeeshop.data.remote.response.ProductVariantResponse
import com.example.coffeeshop.data.repository.CartRepository
import com.example.coffeeshop.data.repository.FavoriteRepository
import com.example.coffeeshop.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoffeeDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _coffee = MutableStateFlow<ProductResponse?>(null)
    val coffee: StateFlow<ProductResponse?> = _coffee.asStateFlow()

    private val _favorites = MutableStateFlow<List<FavoriteProductResponse>>(emptyList())
    val favorites: StateFlow<List<FavoriteProductResponse>> = _favorites.asStateFlow()

    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes: StateFlow<ByteArray?> = _imageBytes.asStateFlow()

    private val _selectedSize = MutableStateFlow<String?>(null)
    val selectedSize: StateFlow<String?> = _selectedSize.asStateFlow()

    private val _isInCartWithCurrentSize = MutableStateFlow(false)
    val isInCartWithCurrentSize: StateFlow<Boolean> = _isInCartWithCurrentSize.asStateFlow()

    internal val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isFavoriteWithCurrentSize: StateFlow<Boolean> = combine(
        _coffee, _selectedSize, _favorites
    ) { product, selectedSize, favorites ->
        if (product == null || selectedSize == null) false
        else favorites.any { it.id == product.id && it.selectedSize == selectedSize }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val availableSizes: StateFlow<List<ProductVariantResponse>> = _coffee
        .map { it?.sizes ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentPrice: StateFlow<String> = combine(_coffee, _selectedSize) { product, selectedSize ->
        if (product == null) return@combine "₽0.00"
        val sizeToUse = selectedSize ?: product.sizes.firstOrNull()?.size ?: "M"
        val sizePrice = product.sizes.find { it.size == sizeToUse }?.price ?: 0f
        "₽${"%.2f".format(sizePrice)}"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₽0.00")

    fun checkIfInCartWithCurrentSize() {
        viewModelScope.launch {
            val currentProduct = _coffee.value ?: return@launch
            val currentSize = _selectedSize.value ?: return@launch
            _isInCartWithCurrentSize.value = cartRepository.isInCart(currentProduct.id, currentSize)
        }
    }

    fun setCoffee(product: ProductResponse, favoriteSize: String = "") {
        _coffee.value = product
        _selectedSize.value = if (favoriteSize.isNotEmpty()) favoriteSize
        else product.sizes.find { it.size == "M" }?.size ?: product.sizes.firstOrNull()?.size
        loadFavorites()
        checkIfInCartWithCurrentSize()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = favoriteRepository.getFavorites()
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentProduct = _coffee.value ?: return@launch
            val currentSize = _selectedSize.value ?: return@launch
            _isLoading.value = true
            try {
                if (isFavoriteWithCurrentSize.value) {
                    if (favoriteRepository.removeFromFavorites(currentProduct.id, currentSize)) loadFavorites()
                } else {
                    if (favoriteRepository.addToFavorites(currentProduct.id, currentSize)) loadFavorites()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectSize(size: String) {
        _selectedSize.value = size
        checkIfInCartWithCurrentSize()
    }

    fun loadCoffeeImage() {
        viewModelScope.launch {
            val imageName = _coffee.value?.imageName ?: return@launch
            _imageBytes.value = productRepository.getProductImage(imageName)
        }
    }

    fun addToCart() {
        viewModelScope.launch {
            val currentProduct = _coffee.value ?: return@launch
            val currentSize = _selectedSize.value ?: return@launch
            _isLoading.value = true
            try {
                cartRepository.addToCart(currentProduct.id, currentSize, 1)
                _isInCartWithCurrentSize.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPriceForSize(size: String): Float {
        return _coffee.value?.sizes?.find { it.size == size }?.price ?: 0f
    }
}

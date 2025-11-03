package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class CoffeeDetailViewModel : ViewModel() {
    private val _selectedSize = MutableStateFlow("M")
    val selectedSize: StateFlow<String> = _selectedSize.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    fun selectSize(size: String) {
        _selectedSize.value = size
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun addToCart(coffeeId: Int) {
        // Реализация добавления в корзину
        println("Added coffee $coffeeId to cart")
    }
}
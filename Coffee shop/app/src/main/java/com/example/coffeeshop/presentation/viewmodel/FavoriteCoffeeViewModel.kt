package com.example.coffeeshop.presentation.viewmodel

import androidx.compose.runtime.mutableStateMapOf
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
import java.net.URLEncoder

class FavoriteCoffeeViewModel(
    internal val repository: CoffeeRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    // ИЗМЕНИТЬ: Теперь храним пары (кофе + сохраненный размер)
    private val _favoriteCoffees = MutableStateFlow<List<Pair<CoffeeResponse, String>>>(emptyList())
    val favoriteCoffees: StateFlow<List<Pair<CoffeeResponse, String>>> = _favoriteCoffees.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _imageMap = mutableStateMapOf<Int, ByteArray?>()

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val favorites = repository.getFavorites(token)
                    val allCoffee = repository.getAllCoffee(token)

                    val favoriteCoffeeList = favorites.mapNotNull { favorite ->
                        val coffee = allCoffee.find { it.id == favorite.id }
                        coffee?.let { it to favorite.selectedSize }
                    }

                    _favoriteCoffees.value = favoriteCoffeeList
                    favoriteCoffeeList.forEach { (coffee, _) ->
                        loadCoffeeImage(coffee.id, coffee.imageName, token)
                    }
                } else {
                    _error.value = "Пользователь не авторизован"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromFavorites(coffeeId: Int) {
        viewModelScope.launch {
            try {
                val token = prefsManager.getToken()
                if (token != null) {
                    val success = repository.removeFromFavorites(token, coffeeId)
                    if (success) {
                        _favoriteCoffees.value = _favoriteCoffees.value.filter { (coffee, _) -> coffee.id != coffeeId }
                        _imageMap.remove(coffeeId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    fun getImageForCoffee(coffeeId: Int): ByteArray? {
        return _imageMap[coffeeId]
    }

    fun clearError() {
        _error.value = null
    }

    fun getPriceForSavedSize(coffee: CoffeeResponse, savedSize: String): Float {
        return coffee.sizes.find { it.size == savedSize }?.price
            ?: getDefaultPrice(coffee)
    }

    fun getDefaultPrice(coffee: CoffeeResponse): Float {
        return coffee.sizes.find { it.size == "M" }?.price ?: coffee.sizes.firstOrNull()?.price ?: 0f
    }

    fun encodeSizesForNavigation(coffee: CoffeeResponse): String {
        return URLEncoder.encode(
            coffee.sizes.joinToString(",") { "${it.size}:${it.price}" },
            "UTF-8"
        )
    }
}
package com.example.coffeeshop.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import com.example.coffeeshop.navigation.NavigationRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import kotlin.collections.filter

class HomeViewModel(
    private val repository: CoffeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _imageCache = mutableStateMapOf<String, ByteArray?>()
    val imageCache: Map<String, ByteArray?> get() = _imageCache


    private val _lastSearchQuery = MutableStateFlow("")
    private val _lastSelectedType = MutableStateFlow<String?>(null)

    fun loadCoffeeData(token: String) {
        viewModelScope.launch {
            try {
                val coffee = repository.getAllCoffee(token)
                val types = repository.getAllCoffeeTypes(token)

                val coffeeTypes = listOf("Все кофе") + types.map { it.type }
                val typeMapping = types.associate { it.type to it.id }

                _uiState.update {
                    it.copy(
                        allCoffee = coffee,
                        filteredCoffee = applySavedFilters(coffee),
                        coffeeTypes = coffeeTypes,
                        coffeeTypeMapping = typeMapping
                    )
                }

                loadCoffeeImages(coffee, token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    internal fun encodeSizesForNavigation(coffee: CoffeeResponse): String {
        return URLEncoder.encode(
            coffee.sizes.joinToString(",") { "${it.size}:${it.price}" },
            "UTF-8"
        )
    }

    private fun applySavedFilters(coffeeList: List<CoffeeResponse>): List<CoffeeResponse> {
        var filtered = coffeeList

        _lastSelectedType.value?.let { typeName ->
            if (typeName != "Все кофе") {
                val typeId = _uiState.value.coffeeTypeMapping[typeName]
                if (typeId != null) {
                    filtered = filtered.filter { it.type.id == typeId }
                }
            }
        }

        if (_lastSearchQuery.value.isNotBlank()) {
            filtered = filtered.filter { coffee ->
                coffee.name.contains(_lastSearchQuery.value, ignoreCase = true) ||
                        coffee.type.type.contains(_lastSearchQuery.value, ignoreCase = true) ||
                        coffee.description.contains(_lastSearchQuery.value, ignoreCase = true)
            }
        }
        return filtered
    }

    fun getDefaultPrice(coffee: CoffeeResponse): Float {
        return coffee.sizes.find { it.size == "M" }?.price ?: coffee.sizes.firstOrNull()?.price ?: 0f
    }


    fun getAvailableSizes(coffee: CoffeeResponse): List<String> {
        return coffee.sizes.map { it.size }
    }


    fun getPriceBySize(coffee: CoffeeResponse, size: String): Float {
        return coffee.sizes.find { it.size == size }?.price ?: getDefaultPrice(coffee)
    }


    private fun loadCoffeeImages(coffeeList: List<CoffeeResponse>, token: String) {
        viewModelScope.launch {
            coffeeList.forEach { coffee ->
                if (!_imageCache.containsKey(coffee.imageName)) {
                    val imageBytes = repository.getCoffeeImage(coffee.imageName, token)
                    _imageCache[coffee.imageName] = imageBytes
                }
            }
        }
    }

    fun onCoffeeTypeSelected(typeName: String) {
        _lastSelectedType.value = typeName

        val currentState = _uiState.value

        if (typeName == "Все кофе") {
            _uiState.update {
                it.copy(
                    selectedTypeName = typeName,
                    selectedTypeId = null,
                    filteredCoffee = if (it.isSearching) it.searchResults else it.allCoffee
                )
            }
        } else {
            val typeId = currentState.coffeeTypeMapping[typeName]
            val filtered = if (currentState.isSearching) {
                currentState.searchResults.filter { it.type.id == typeId }
            } else {
                currentState.allCoffee.filter { it.type.id == typeId }
            }

            _uiState.update {
                it.copy(
                    selectedTypeName = typeName,
                    selectedTypeId = typeId,
                    filteredCoffee = filtered
                )
            }
        }
    }

    fun searchCoffee(query: String) {
        _lastSearchQuery.value = query

        val currentState = _uiState.value
        val isSearching = query.isNotBlank()

        if (!isSearching) {
            _uiState.update {
                it.copy(
                    isSearching = false,
                    searchResults = emptyList(),
                    filteredCoffee = if (it.selectedTypeId == null) it.allCoffee
                    else it.allCoffee.filter { coffee -> coffee.type.id == it.selectedTypeId }
                )
            }
            return
        }

        val searchResults = currentState.allCoffee.filter { coffee ->
            coffee.name.contains(query, ignoreCase = true) ||
                    coffee.type.type.contains(query, ignoreCase = true) ||
                    coffee.description.contains(query, ignoreCase = true)
        }

        val finalFiltered = if (currentState.selectedTypeId != null) {
            searchResults.filter { it.type.id == currentState.selectedTypeId }
        } else {
            searchResults
        }

        _uiState.update {
            it.copy(
                isSearching = true,
                searchResults = searchResults,
                filteredCoffee = finalFiltered
            )
        }
    }

    fun getCurrentSelectedType(): String? {
        return _lastSelectedType.value
    }

    fun getCurrentSearchQuery(): String {
        return _lastSearchQuery.value
    }

    fun clearSavedState() {
        _lastSearchQuery.value = ""
        _lastSelectedType.value = null
    }
}

data class HomeUiState(
    val allCoffee: List<CoffeeResponse> = emptyList(),
    val filteredCoffee: List<CoffeeResponse> = emptyList(),
    val coffeeTypes: List<String> = emptyList(),
    val coffeeTypeMapping: Map<String, Int> = emptyMap(),
    val selectedTypeId: Int? = null,
    val selectedTypeName: String = "Все кофе",
    val searchResults: List<CoffeeResponse> = emptyList(),
    val isSearching: Boolean = false
)
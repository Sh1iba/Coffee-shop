package com.example.coffeeshop.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.api.ApiClient
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.filter

class HomeViewModel(
    private val repository: CoffeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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
                        filteredCoffee = coffee,
                        coffeeTypes = coffeeTypes,
                        coffeeTypeMapping = typeMapping
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onCoffeeTypeSelected(typeName: String) {
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
package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.remote.response.NominatimAddress
import com.example.coffeeshop.data.repository.AddressRepository
import com.example.coffeeshop.data.managers.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val locationManager: LocationManager,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationState())
    val uiState: StateFlow<LocationState> = _uiState.asStateFlow()

    init {
        loadSavedLocation()
    }

    private fun loadSavedLocation() {
        _uiState.value = _uiState.value.copy(
            selectedAddress = locationManager.getSavedLocation()
        )
    }

    fun onShowAddressDialogChange(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddressDialog = show)
    }

    fun onAddressSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(addressSearchQuery = query)

        if (query.length >= 2) {
            searchAddresses(query)
        } else {
            _uiState.value = _uiState.value.copy(addressSearchResults = emptyList())
        }
    }

    fun onAddressSelected(address: String) {
        _uiState.value = _uiState.value.copy(
            selectedAddress = address,
            showAddressDialog = false,
            addressSearchQuery = ""
        )
        saveLocation(address)
    }

    private fun searchAddresses(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddressLoading = true)
            try {
                val results = addressRepository.searchAddress(query)
                _uiState.value = _uiState.value.copy(
                    addressSearchResults = results,
                    isAddressLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка поиска адреса",
                    isAddressLoading = false
                )
            }
        }
    }

    private fun saveLocation(address: String) {
        locationManager.saveLocation(address)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LocationState(
    val selectedAddress: String = "",
    val addressSearchQuery: String = "",
    val addressSearchResults: List<NominatimAddress> = emptyList(),
    val isAddressLoading: Boolean = false,
    val showAddressDialog: Boolean = false,
    val error: String? = null
)
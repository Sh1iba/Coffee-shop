package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.repository.SellerRepository
import com.example.coffeeshop.domain.SellerRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BecomeSellerViewModel @Inject constructor(
    private val sellerRepository: SellerRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun register(name: String, description: String, category: String) {
        if (name.isBlank() || description.isBlank() || category.isBlank()) {
            _error.value = "Заполните все поля"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = sellerRepository.becomeSeller(SellerRequest(name.trim(), description.trim(), category.trim()))
            if (result != null) {
                prefsManager.saveRole("SELLER")
                _success.value = true
            } else {
                _error.value = "Не удалось зарегистрироваться. Попробуйте ещё раз"
            }
            _isLoading.value = false
        }
    }

    fun clearError() { _error.value = null }
}

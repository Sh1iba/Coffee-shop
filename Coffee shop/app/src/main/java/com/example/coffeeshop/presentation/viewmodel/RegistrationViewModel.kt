package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.ErrorParser
import com.example.coffeeshop.data.remote.api.ApiClient
import com.example.coffeeshop.domain.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val errorParser: ErrorParser
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationState())
    val uiState: StateFlow<RegistrationState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onPasswordVisibilityChange() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onShowSuccessDialogChange(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSuccessDialog = show)
    }

    fun register() {
        val currentState = _uiState.value

        if (currentState.name.isEmpty() || currentState.email.isEmpty() || currentState.password.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "Все поля должны быть заполнены")
            return
        }

        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val request = RegisterRequest(currentState.email, currentState.password, currentState.name)
                val response = ApiClient.coffeeApi.registerUser(request)

                if (response.isSuccessful) {
                    response.body()?.let {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            showSuccessDialog = true
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    val errorMessage = errorParser.parseErrorMessage(errorBody) ?: "Неизвестная ошибка"
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Ошибка сети: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class RegistrationState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false
)
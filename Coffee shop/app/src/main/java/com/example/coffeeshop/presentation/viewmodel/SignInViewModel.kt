package com.example.coffeeshop.presentation.viewmodel

import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.domain.LoginRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.remote.api.ApiClient
import com.example.coffeeshop.data.managers.ErrorParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val prefsManager: PrefsManager,
    private val errorParser: ErrorParser
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInState())
    val uiState: StateFlow<SignInState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onPasswordVisibilityChange() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun login() {
        val currentState = _uiState.value

        if (currentState.email.isEmpty() || currentState.password.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "Все поля должны быть заполнены")
            return
        }

        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val request = LoginRequest(currentState.email, currentState.password)
                val response = ApiClient.coffeeApi.loginUser(request)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        prefsManager.saveUserData(
                            token = loginResponse.token,
                            userId = loginResponse.userId,
                            email = loginResponse.email,
                            name = loginResponse.name
                        )
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            isLoginSuccess = true
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorParser.parseErrorMessage(errorBody ?: "")
                        ?: "Ошибка сервера: ${response.code()}"
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

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)
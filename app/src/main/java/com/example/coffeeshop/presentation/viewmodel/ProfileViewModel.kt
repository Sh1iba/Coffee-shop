package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.remote.response.LoginResponse
import com.example.coffeeshop.data.repository.ProfileRepository
import com.example.coffeeshop.domain.UpdateProfileRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<LoginResponse?>(null)
    val profile: StateFlow<LoginResponse?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _profile.value = profileRepository.getProfile()
            _isLoading.value = false
        }
    }

    fun updateProfile(name: String? = null, currentPassword: String? = null, newPassword: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = profileRepository.updateProfile(
                UpdateProfileRequest(name = name, currentPassword = currentPassword, newPassword = newPassword)
            )
            if (result != null) {
                _profile.value = result
                _updateSuccess.value = true
            } else {
                _error.value = "Не удалось обновить профиль"
            }
            _isLoading.value = false
        }
    }

    fun clearError() { _error.value = null }
    fun clearUpdateSuccess() { _updateSuccess.value = false }
}

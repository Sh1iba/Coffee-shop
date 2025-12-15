package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _darkModeState = MutableStateFlow(prefsManager.getBoolean(PrefsManager.KEY_DARK_MODE, false))
    val darkModeState: StateFlow<Boolean> = _darkModeState

    private val _notificationsState = MutableStateFlow(prefsManager.getBoolean(PrefsManager.KEY_NOTIFICATIONS, true))
    val notificationsState: StateFlow<Boolean> = _notificationsState

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            prefsManager.saveBoolean(PrefsManager.KEY_DARK_MODE, enabled)
            _darkModeState.value = enabled
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            prefsManager.saveBoolean(PrefsManager.KEY_NOTIFICATIONS, enabled)
            _notificationsState.value = enabled
        }
    }
}
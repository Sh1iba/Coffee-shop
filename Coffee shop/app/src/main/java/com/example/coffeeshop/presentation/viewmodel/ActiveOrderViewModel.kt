package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.PrefsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActiveOrderState(
    val timeLeft: Int = 0,
    val isOrderDelivered: Boolean = false,
    val progress: Float = 0f,
    val minutes: Int = 0,
    val seconds: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ActiveOrderEvent {
    object StartTimer : ActiveOrderEvent()
    object ResetTimer : ActiveOrderEvent()
    object NavigateToHome : ActiveOrderEvent()
}

class ActiveOrderViewModel(
    private val prefsManager: PrefsManager,
    private val deliveryTimeMinutes: Float = 0.5f
) : ViewModel() {

    private val _state = MutableStateFlow(ActiveOrderState())
    val state: StateFlow<ActiveOrderState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ActiveOrderEvent>()
    val events: SharedFlow<ActiveOrderEvent> = _events.asSharedFlow()

    private val totalSeconds = (deliveryTimeMinutes * 60).toInt()

    init {
        startTimer()
        saveOrderStartTime()
    }

    private fun saveOrderStartTime() {
        prefsManager.saveLong("order_start_ts", System.currentTimeMillis())
    }

    private fun startTimer() {
        viewModelScope.launch {
            _state.update { it.copy(timeLeft = totalSeconds) }

            while (_state.value.timeLeft > 0) {
                delay(1000)
                updateTimeLeft()
            }

            _state.update { it.copy(isOrderDelivered = true) }
        }
    }

    private fun updateTimeLeft() {
        val currentTimeLeft = _state.value.timeLeft - 1

        _state.update {
            it.copy(
                timeLeft = currentTimeLeft,
                minutes = currentTimeLeft / 60,
                seconds = currentTimeLeft % 60,
                progress = 1f - (currentTimeLeft.toFloat() / totalSeconds.toFloat())
            )
        }
    }

    fun onEvent(event: ActiveOrderEvent) {
        viewModelScope.launch {
            when (event) {
                is ActiveOrderEvent.StartTimer -> {
                    startTimer()
                    saveOrderStartTime()
                }
                is ActiveOrderEvent.ResetTimer -> {
                    resetTimer()
                }
                is ActiveOrderEvent.NavigateToHome -> {
                    resetTimer()
                    _events.emit(event)
                }
            }
        }
    }

    private fun resetTimer() {
        prefsManager.saveLong("order_start_ts", 0)
        _state.update { ActiveOrderState() }
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
    }
}
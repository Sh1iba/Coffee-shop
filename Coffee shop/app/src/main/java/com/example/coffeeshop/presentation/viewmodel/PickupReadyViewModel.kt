package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

data class PickupOrderState(
    val timeLeft: Int = 0,
    val isOrderReady: Boolean = false,
    val progress: Float = 0f,
    val minutes: Int = 0,
    val seconds: Int = 0,
    val orderNumber: String = "#${Random.nextInt(1000, 10000)}",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class PickupOrderEvent {
    object StartTimer : PickupOrderEvent()
    object ResetTimer : PickupOrderEvent()
    object GenerateNewOrderNumber : PickupOrderEvent()
    object NavigateToHome : PickupOrderEvent()
}

@HiltViewModel
class PickupReadyViewModel @Inject constructor() : ViewModel() {

    private val preparationTimeMinutes: Float = 0.5f
    private val totalSeconds = (preparationTimeMinutes * 60).toInt()

    private val _state = MutableStateFlow(PickupOrderState())
    val state: StateFlow<PickupOrderState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PickupOrderEvent>()
    val events: SharedFlow<PickupOrderEvent> = _events.asSharedFlow()

    init { startTimer() }

    fun onEvent(event: PickupOrderEvent) {
        viewModelScope.launch {
            when (event) {
                is PickupOrderEvent.StartTimer -> startTimer()
                is PickupOrderEvent.ResetTimer -> resetTimer()
                is PickupOrderEvent.GenerateNewOrderNumber ->
                    _state.update { it.copy(orderNumber = "#${Random.nextInt(1000, 10000)}") }
                is PickupOrderEvent.NavigateToHome -> _events.emit(event)
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            _state.update { it.copy(timeLeft = totalSeconds, isOrderReady = false, progress = 0f) }
            while (_state.value.timeLeft > 0) {
                delay(1000)
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
            _state.update { it.copy(isOrderReady = true, progress = 1f) }
        }
    }

    private fun resetTimer() { _state.update { PickupOrderState() } }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
    }
}

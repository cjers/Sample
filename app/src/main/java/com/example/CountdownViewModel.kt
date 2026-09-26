package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant

class CountdownViewModel : ViewModel() {

    private val _timezoneOption = MutableStateFlow(TimezoneOption.CENTRAL_TIME)
    val timezoneOption: StateFlow<TimezoneOption> = _timezoneOption.asStateFlow()

    private val _simulatedExpired = MutableStateFlow(false)
    val simulatedExpired: StateFlow<Boolean> = _simulatedExpired.asStateFlow()

    private val _uiState = MutableStateFlow(
        CountdownCalculator.calculate(TimezoneOption.CENTRAL_TIME)
    )
    val uiState: StateFlow<CountdownUiState> = _uiState.asStateFlow()

    init {
        startCountdownTicker()
    }

    private fun startCountdownTicker() {
        viewModelScope.launch {
            while (isActive) {
                val now = Instant.now()
                _uiState.value = CountdownCalculator.calculate(
                    option = _timezoneOption.value,
                    now = now
                )
                delay(50L) // 20 updates per second for smooth countdown
            }
        }
    }

    fun setTimezoneOption(option: TimezoneOption) {
        _timezoneOption.value = option
        _uiState.value = CountdownCalculator.calculate(
            option = option,
            now = Instant.now()
        )
    }

    fun toggleSimulatedExpiration() {
        _simulatedExpired.value = !_simulatedExpired.value
    }
}

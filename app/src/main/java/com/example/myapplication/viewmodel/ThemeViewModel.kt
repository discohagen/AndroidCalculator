package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.ThemeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {
    private val _state = MutableStateFlow(ThemeState())
    val themeState: StateFlow<ThemeState> = _state.asStateFlow()

    fun onChange() {
        _state.value = _state.value.copy(
            isDark = !_state.value.isDark
        )
    }

    fun isDark(): Boolean {
        return themeState.value.isDark
    }

}
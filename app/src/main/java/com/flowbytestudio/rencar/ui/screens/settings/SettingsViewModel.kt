package com.flowbytestudio.rencar.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.settings.ThemeController
import com.flowbytestudio.rencar.data.settings.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ThemeController.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

    fun onThemeModeSelected(mode: ThemeMode) {
        ThemeController.setThemeMode(mode)
    }
}

package com.flowbytestudio.rencar.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import com.flowbytestudio.rencar.data.settings.OnboardingPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onPageChanged(page: Int) {
        _uiState.value = _uiState.value.copy(currentPage = page)
    }

    fun onGoToPreviousPage(): Boolean {
        val current = _uiState.value.currentPage
        if (current == 0) return false
        _uiState.value = _uiState.value.copy(currentPage = current - 1)
        return true
    }

    fun onOnboardingCompleted() {
        OnboardingPreferences.markOnboardingSeen()
    }
}

package com.flowbytestudio.rencar.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.auth.AuthSession
import com.flowbytestudio.rencar.data.license.LicenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val licenseRepository: LicenseRepository = LicenseRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AuthSession.currentUser.collect { user ->
                _uiState.update {
                    it.copy(
                        name = user?.fullName.orEmpty(),
                        phone = user?.phone.orEmpty(),
                        avatarUrl = user?.avatarUrl,
                    )
                }
            }
        }
        loadLicenseStatus()
    }

    private fun loadLicenseStatus() {
        viewModelScope.launch {
            licenseRepository.getStatus().onSuccess { status ->
                _uiState.update { it.copy(isLicenseVerified = status.status == "APPROVED") }
            }
        }
    }

    fun onLogoutClick() {
        _uiState.update { it.copy(isLoggingOut = true) }
        AuthSession.clear()
    }
}

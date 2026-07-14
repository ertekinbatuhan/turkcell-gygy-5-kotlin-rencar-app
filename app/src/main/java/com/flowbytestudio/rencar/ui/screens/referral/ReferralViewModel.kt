package com.flowbytestudio.rencar.ui.screens.referral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.auth.AuthRepository
import com.flowbytestudio.rencar.data.auth.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReferralViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReferralUiState(referralCode = AuthSession.currentUser.value?.referralCode)
    )
    val uiState: StateFlow<ReferralUiState> = _uiState.asStateFlow()

    init {
        if (_uiState.value.referralCode == null) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                repository.me().onSuccess { user ->
                    _uiState.update { it.copy(referralCode = user.referralCode, isLoading = false) }
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}

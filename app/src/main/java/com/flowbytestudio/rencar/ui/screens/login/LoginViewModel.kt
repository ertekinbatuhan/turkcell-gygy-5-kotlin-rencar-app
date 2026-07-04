package com.flowbytestudio.rencar.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone, error = null) }
    }

    fun onCodeChange(code: String) {
        _uiState.update { it.copy(code = code, error = null) }
    }

    fun onRequestOtp() {
        val phone = _uiState.value.phone.trim()
        if (phone.isBlank()) {
            _uiState.update { it.copy(error = "Telefon numaranızı girin.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.requestOtp(phone)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, step = LoginStep.OTP) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, error = throwable.toUserMessage())
                    }
                }
        }
    }

    fun onVerifyOtp() {
        val phone = _uiState.value.phone.trim()
        val code = _uiState.value.code.trim()
        if (code.length != 6) {
            _uiState.update { it.copy(error = "6 haneli kodu eksiksiz girin.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.verifyOtp(phone, code)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, error = throwable.toUserMessage())
                    }
                }
        }
    }

    fun onChangePhone() {
        _uiState.update { it.copy(step = LoginStep.PHONE, code = "", error = null) }
    }

    private fun Throwable.toUserMessage(): String = when {
        message?.contains("401") == true -> "Kod hatalı, süresi dolmuş veya bu numara kayıtlı değil."
        else -> "Bir şeyler ters gitti. Lütfen tekrar deneyin."
    }
}

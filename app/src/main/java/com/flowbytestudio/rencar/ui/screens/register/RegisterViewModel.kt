package com.flowbytestudio.rencar.ui.screens.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, error = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        if (digitsOnly.length <= 10) {
            _uiState.update { it.copy(phone = digitsOnly, error = null) }
        }
    }

    fun onRegister() {
        val state = _uiState.value

        if (state.fullName.isBlank() || state.email.isBlank() || state.phone.length != 10) {
            _uiState.update { it.copy(error = "Lütfen tüm alanları eksiksiz doldurun.") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(error = "Parola en az 6 karakter olmalı.") }
            return
        }

        val fullPhone = "+90${state.phone}"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.register(
                    email = state.email,
                    password = state.password,
                    fullName = state.fullName,
                    phone = fullPhone,
                )
                    .onSuccess {
                        Log.d("RencarAuth", "Register Success: phone=$fullPhone")
                        _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                    }
                    .onFailure { throwable ->
                        val statusCode = (throwable as? HttpException)?.code()
                        Log.e("RencarAuth", "Register Failure: phone=$fullPhone, status=$statusCode", throwable)

                        val errorMessage = when {
                            throwable is IOException -> "Bağlantı hatası oluştu."
                            statusCode == 409 -> "Bu e-posta adresi zaten kayıtlı."
                            else -> "Kayıt oluşturulamadı. Bilgilerinizi kontrol edin."
                        }
                        _uiState.update { it.copy(isLoading = false, error = errorMessage) }
                    }
            } catch (e: Exception) {
                Log.e("RencarAuth", "Register Exception: phone=$fullPhone", e)
                _uiState.update { it.copy(isLoading = false, error = "Bağlantı hatası oluştu.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

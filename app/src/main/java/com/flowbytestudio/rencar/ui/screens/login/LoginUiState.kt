package com.flowbytestudio.rencar.ui.screens.login

enum class LoginStep { PHONE, OTP }

data class LoginUiState(
    val step: LoginStep = LoginStep.PHONE,
    val phone: String = "+905550000000",
    val code: String = "123456",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
)

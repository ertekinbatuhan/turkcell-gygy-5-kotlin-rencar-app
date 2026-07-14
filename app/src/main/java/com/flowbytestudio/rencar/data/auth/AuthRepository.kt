package com.flowbytestudio.rencar.data.auth

import com.flowbytestudio.rencar.data.network.NetworkModule

class AuthRepository(
    private val api: AuthApi = NetworkModule.authApi,
) {

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        referralCode: String? = null,
    ): Result<AuthResponse> = runCatching {
        api.register(
            RegisterRequest(
                email = email,
                password = password,
                fullName = fullName,
                phone = phone,
                referralCode = referralCode,
            )
        )
    }.onSuccess { response ->
        AuthSession.onAuthenticated(response, isNewRegistration = true)
    }

    suspend fun requestOtp(phone: String): Result<OtpRequiredResponse> = runCatching {
        api.login(LoginRequest(phone = phone))
    }

    suspend fun verifyOtp(phone: String, code: String): Result<AuthResponse> = runCatching {
        api.verifyOtp(VerifyOtpRequest(phone = phone, code = code))
    }.onSuccess { response ->
        AuthSession.onAuthenticated(response)
    }

    suspend fun me(): Result<UserResponse> = runCatching {
        api.me()
    }.onSuccess { user ->
        AuthSession.updateCurrentUser(user)
    }
}

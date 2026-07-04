package com.flowbytestudio.rencar.data.auth

import com.flowbytestudio.rencar.data.network.NetworkModule

class AuthRepository(
    private val api: AuthApi = NetworkModule.authApi,
) {

    suspend fun requestOtp(phone: String): Result<OtpRequiredResponse> = runCatching {
        api.login(LoginRequest(phone = phone))
    }

    suspend fun verifyOtp(phone: String, code: String): Result<AuthResponse> = runCatching {
        api.verifyOtp(VerifyOtpRequest(phone = phone, code = code))
    }.onSuccess { response ->
        AuthSession.onAuthenticated(response)
    }
}

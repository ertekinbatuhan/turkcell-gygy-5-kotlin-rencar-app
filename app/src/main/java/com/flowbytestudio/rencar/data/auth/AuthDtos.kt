package com.flowbytestudio.rencar.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    // Davet kodu (D6) — verilirse kayıt davet edene bağlanır, geçersiz kod 400 döner.
    val referralCode: String? = null,
)

@Serializable
data class LoginRequest(val phone: String)

@Serializable
data class OtpRequiredResponse(
    val message: String,
    val phone: String,
    val expiresAt: String,
)

@Serializable
data class VerifyOtpRequest(val phone: String, val code: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse,
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val phone: String? = null,
    val fullName: String,
    val role: String,
    // Backend UserResponseDto'da henüz yok; profil avatarı için önden eklendi.
    val avatarUrl: String? = null,
    // /auth/me çağrısında yoksa üretilir; diğer cevaplarda null olabilir (D6).
    val referralCode: String? = null,
)

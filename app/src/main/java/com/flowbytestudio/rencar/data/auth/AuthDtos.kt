package com.flowbytestudio.rencar.data.auth

import kotlinx.serialization.Serializable

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
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse,
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
)

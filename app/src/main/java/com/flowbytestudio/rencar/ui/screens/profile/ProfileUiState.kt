package com.flowbytestudio.rencar.ui.screens.profile

data class ProfileUiState(
    val name: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val isLicenseVerified: Boolean = false,
    val licenseClass: String = "B sınıfı · geçerli",
    val isLoggingOut: Boolean = false,
)

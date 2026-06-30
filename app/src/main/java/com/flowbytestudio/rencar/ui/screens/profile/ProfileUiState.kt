package com.flowbytestudio.rencar.ui.screens.profile

data class ProfileUiState(
    val name: String = "Deniz Yılmaz",
    val phone: String = "+90 532 000 00 00",
    val isLicenseVerified: Boolean = true,
    val licenseClass: String = "B sınıfı · geçerli",
    val isLoggingOut: Boolean = false,
)

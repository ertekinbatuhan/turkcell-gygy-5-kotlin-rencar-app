package com.flowbytestudio.rencar.ui.screens.profile

import androidx.annotation.StringRes
import com.flowbytestudio.rencar.R

data class ProfileUiState(
    val name: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    // PENDING / CUSTOMER / ADMIN
    val role: String? = null,
    // /auth/me ile üretilir; gelene kadar null.
    val referralCode: String? = null,
    // UNKNOWN (henüz yüklenmedi/hata) / NOT_SUBMITTED / UNDER_REVIEW / APPROVED / REJECTED.
    // Varsayılan UNKNOWN: getStatus başarısız olursa onaylı kullanıcıya yanlışlıkla
    // "ehliyetini doğrula" gösterilmez; durum kartı bilinene kadar gizli kalır.
    val licenseStatus: String = "UNKNOWN",
    val rejectReason: String? = null,
    @StringRes val licenseClass: Int = R.string.profile_license_class_default,
    // Bu ayki yolculuk özeti (CUSTOMER olmayan kullanıcıda null kalır).
    val stats: ProfileStats? = null,
    val isRefreshingSession: Boolean = false,
    val isLoggingOut: Boolean = false,
) {
    // Ehliyet onayı sonrası CUSTOMER token'ı için oturum yenileme önerilir.
    val canRefreshSession: Boolean
        get() = licenseStatus == "APPROVED" && role != null && role != "CUSTOMER"
}

data class ProfileStats(
    val tripCount: Int,
    val totalSpent: Double,
    val totalMinutes: Int,
    val totalKm: Double,
)

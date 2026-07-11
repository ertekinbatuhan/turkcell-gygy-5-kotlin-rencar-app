package com.flowbytestudio.rencar.ui.screens.license

import android.net.Uri

enum class LicenseUploadStep { LICENSE, SELFIE, CONFIRM }

data class LicenseUploadUiState(
    val step: LicenseUploadStep = LicenseUploadStep.LICENSE,
    val frontUri: Uri? = null,
    val backUri: Uri? = null,
    val selfieUri: Uri? = null,
    val selfieUrl: String? = null,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
) {
    val hasSelfie: Boolean get() = selfieUri != null || selfieUrl != null
}

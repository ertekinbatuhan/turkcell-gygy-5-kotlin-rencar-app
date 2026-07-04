package com.flowbytestudio.rencar.data.license

import kotlinx.serialization.Serializable

@Serializable
data class LicenseStatusResponse(
    val status: String,
    val frontImageUrl: String? = null,
    val backImageUrl: String? = null,
    val rejectReason: String? = null,
    val reviewedAt: String? = null,
)

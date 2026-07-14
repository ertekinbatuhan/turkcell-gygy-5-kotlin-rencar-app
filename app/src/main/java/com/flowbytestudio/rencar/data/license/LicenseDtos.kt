package com.flowbytestudio.rencar.data.license

import kotlinx.serialization.Serializable

@Serializable
data class LicenseStatusResponse(
    val status: String,
    val frontImageUrl: String? = null,
    val backImageUrl: String? = null,
    val selfieImageUrl: String? = null,
    val rejectReason: String? = null,
    val reviewedAt: String? = null,
)

@Serializable
data class LicenseResponse(
    val id: String,
    val status: String,
    val frontImageUrl: String,
    val backImageUrl: String,
    val selfieImageUrl: String? = null,
    val rejectReason: String? = null,
    val reviewedAt: String? = null,
    val createdAt: String,
    val updatedAt: String,
)

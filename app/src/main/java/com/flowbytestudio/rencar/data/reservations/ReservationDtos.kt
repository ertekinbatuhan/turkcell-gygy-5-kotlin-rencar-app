package com.flowbytestudio.rencar.data.reservations

import kotlinx.serialization.Serializable

@Serializable
data class CreateReservationRequest(
    val vehicleId: String,
)

@Serializable
data class ReservationVehicleSummary(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerMinute: Double,
)

@Serializable
data class ReservationResponse(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: ReservationVehicleSummary? = null,
    // ACTIVE / CONVERTED / CANCELLED / EXPIRED
    val status: String,
    val expiresAt: String,
    // 0 tabanlı, negatif olmaz.
    val remainingSeconds: Long,
    val createdAt: String,
)

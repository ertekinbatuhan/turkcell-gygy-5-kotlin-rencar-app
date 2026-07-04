package com.flowbytestudio.rencar.ui.screens.history

enum class RentalStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED,
}

data class RentalUiModel(
    val id: String,
    val vehicleId: String,
    val vehicleLabel: String,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: RentalStatus,
    // durationMinutes/distanceKm are mock-only: RentalResponseDto has no such fields yet.
    // Replace with real values once the backend exposes trip telemetry.
    val durationMinutes: Int,
    val distanceKm: Double,
)

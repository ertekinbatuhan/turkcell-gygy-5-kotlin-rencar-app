package com.flowbytestudio.rencar.ui.screens.reservation

import com.flowbytestudio.rencar.data.vehicles.VehicleDto

data class ReservationUiState(
    val isLoadingVehicle: Boolean = true,
    val vehicle: VehicleDto? = null,
    val loadError: String? = null,
    val days: Int = 1,
    val termsAccepted: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val reservationCompleted: Boolean = false,
    val completedTotalPrice: Double? = null,
) {
    val totalPrice: Double
        get() = (vehicle?.pricePerDay ?: 0.0) * days

    val canSubmit: Boolean
        get() = vehicle != null && termsAccepted && !isSubmitting
}

package com.flowbytestudio.rencar.ui.screens.reservation

import com.flowbytestudio.rencar.data.vehicles.VehicleDto

enum class RentalPlan { DAKIKALIK, SAATLIK, GUNLUK }

// Backend dakikalık plan detaylarını (ücretsiz rezervasyon süresi, başlangıç ücreti)
// henüz döndürmüyor; tasarım hazırlığı için sabit değerler.
const val FREE_RESERVATION_MINUTES = 15
const val MINUTE_PLAN_START_FEE = 15.0
const val MINUTE_ESTIMATE_MINUTES = 30

data class ReservationUiState(
    val isLoadingVehicle: Boolean = true,
    val vehicle: VehicleDto? = null,
    val loadError: String? = null,
    val selectedPlan: RentalPlan = RentalPlan.GUNLUK,
    val days: Int = 1,
    val termsAccepted: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val reservationCompleted: Boolean = false,
    val completedTotalPrice: Double? = null,
) {
    val totalPrice: Double
        get() = (vehicle?.pricePerDay ?: 0.0) * days

    /** Dakikalık plan için tahmini ücret: başlangıç ücreti + 30 dk kullanım. */
    val minuteEstimate: Double?
        get() = vehicle?.pricePerMinute?.let { MINUTE_PLAN_START_FEE + it * MINUTE_ESTIMATE_MINUTES }

    val canSubmit: Boolean
        get() = vehicle != null && termsAccepted && !isSubmitting
}

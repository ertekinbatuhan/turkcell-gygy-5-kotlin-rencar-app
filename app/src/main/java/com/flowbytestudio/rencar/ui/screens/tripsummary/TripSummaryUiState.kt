package com.flowbytestudio.rencar.ui.screens.tripsummary

import com.flowbytestudio.rencar.data.rentals.RentalDto
import com.flowbytestudio.rencar.data.vehicles.VehicleDto

data class TripSummaryUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val rental: RentalDto? = null,
    val vehicle: VehicleDto? = null,
    /** Yolculuk süresi (dk). startDate/endDate'ten hesaplanır. */
    val durationMinutes: Long? = null,
    // Backend'de ödeme/indirim/kart endpoint'i henüz yok; tasarım hazırlığı için
    // nullable alanlar. Null kaldıkça ilgili satırlar ekranda gizlenir.
    val startFee: Double? = null,
    val serviceFee: Double? = null,
    val discountLabel: String? = null,
    val discountAmount: Double? = null,
    val cardLast4: String? = null,
    val cardLabel: String? = null,
) {
    val totalAmount: Double?
        get() = rental?.totalPrice?.let { base ->
            base + (startFee ?: 0.0) + (serviceFee ?: 0.0) - (discountAmount ?: 0.0)
        }
}

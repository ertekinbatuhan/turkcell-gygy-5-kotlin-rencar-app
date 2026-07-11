package com.flowbytestudio.rencar.ui.screens.activerental

import com.flowbytestudio.rencar.data.rentals.RentalDto
import com.flowbytestudio.rencar.data.vehicles.VehicleDto

data class ActiveRentalUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val rental: RentalDto? = null,
    val vehicle: VehicleDto? = null,
    val elapsedSeconds: Long = 0L,
    val isEnding: Boolean = false,
    val endError: String? = null,
    val endedTotalPrice: Double? = null,
) {
    /** Dakikalık fiyat API'ye eklenene kadar null kalır; ekran "—" gösterir. */
    val currentCost: Double?
        get() = vehicle?.pricePerMinute?.let { it * (elapsedSeconds / 60.0) }
}

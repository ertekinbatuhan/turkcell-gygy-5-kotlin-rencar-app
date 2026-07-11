package com.flowbytestudio.rencar.ui.screens.handover

import com.flowbytestudio.rencar.data.vehicles.VehicleDto

enum class PhotoSide(val label: String) {
    ON("Ön"),
    ARKA("Arka"),
    SOL("Sol"),
    SAG("Sağ"),
}

data class HandoverUiState(
    val isLoadingVehicle: Boolean = true,
    val vehicle: VehicleDto? = null,
    val loadError: String? = null,
    val capturedSides: Set<PhotoSide> = emptySet(),
    val isStarting: Boolean = false,
    val startError: String? = null,
    val startedRentalId: String? = null,
) {
    val capturedCount: Int
        get() = capturedSides.size

    val remainingCount: Int
        get() = PhotoSide.entries.size - capturedCount

    val canStart: Boolean
        get() = vehicle != null && remainingCount == 0 && !isStarting
}

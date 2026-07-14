package com.flowbytestudio.rencar.ui.screens.handover

import com.flowbytestudio.rencar.data.rentals.RentalVehicleSummary

// UI etiketi Türkçe, API yönü İngilizce: ON=FRONT, ARKA=BACK, SOL=LEFT, SAG=RIGHT.
enum class PhotoSide(val label: String, val apiSide: String) {
    ON("Ön", "FRONT"),
    ARKA("Arka", "BACK"),
    SOL("Sol", "LEFT"),
    SAG("Sağ", "RIGHT");

    companion object {
        fun fromApi(apiSide: String): PhotoSide? = entries.firstOrNull { it.apiSide == apiSide }
    }
}

data class HandoverUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val vehicle: RentalVehicleSummary? = null,
    // Yön -> sunucuda yüklü fotoğrafın URL'i (Coil ile gösterilir).
    val photos: Map<PhotoSide, String> = emptyMap(),
    val uploadedCount: Int = 0,
    val photosComplete: Boolean = false,
    // Şu an yükleniyor olan yönler (slot üzerinde spinner).
    val uploadingSides: Set<PhotoSide> = emptySet(),
    val uploadError: String? = null,
    val isStarting: Boolean = false,
    val startError: String? = null,
    val startedRentalId: String? = null,
    val showCancelDialog: Boolean = false,
    val isCancelling: Boolean = false,
    val cancelError: String? = null,
    val cancelled: Boolean = false,
) {
    val remainingCount: Int
        get() = PhotoSide.entries.size - uploadedCount

    val canStart: Boolean
        get() = photosComplete && !isStarting && !isCancelling && uploadingSides.isEmpty()
}

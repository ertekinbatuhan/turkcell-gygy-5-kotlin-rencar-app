package com.flowbytestudio.rencar.ui.screens.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HistoryUiState(
            rentals = listOf(
                RentalUiModel(
                    id = "r1",
                    vehicleId = "v1",
                    vehicleLabel = "Renault Clio",
                    startDate = "26 Haz 2026 · 14:32",
                    endDate = "26 Haz 2026",
                    totalPrice = 110.50,
                    status = RentalStatus.COMPLETED,
                    durationMinutes = 24,
                    distanceKm = 12.4,
                ),
                RentalUiModel(
                    id = "r2",
                    vehicleId = "v2",
                    vehicleLabel = "Fiat Egea",
                    startDate = "24 Haz 2026 · 18:05",
                    endDate = "24 Haz 2026",
                    totalPrice = 86.00,
                    status = RentalStatus.COMPLETED,
                    durationMinutes = 18,
                    distanceKm = 8.1,
                ),
                RentalUiModel(
                    id = "r3",
                    vehicleId = "v3",
                    vehicleLabel = "Volkswagen Polo",
                    startDate = "21 Haz 2026 · 09:48",
                    endDate = "22 Haz 2026",
                    totalPrice = 142.00,
                    status = RentalStatus.CANCELLED,
                    durationMinutes = 31,
                    distanceKm = 19.6,
                ),
                RentalUiModel(
                    id = "r4",
                    vehicleId = "v1",
                    vehicleLabel = "Renault Clio",
                    startDate = "18 Haz 2026 · 20:14",
                    endDate = "18 Haz 2026",
                    totalPrice = 64.50,
                    status = RentalStatus.COMPLETED,
                    durationMinutes = 14,
                    distanceKm = 6.2,
                ),
            ),
        ),
    )
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    // TODO: replace mock rentals with GET /rentals once a networking client exists.
    // Note: real RentalResponseDto only has vehicleId — vehicleLabel needs a separate
    // GET /vehicles/{id} lookup or a lightweight local cache, not a nested field.
}

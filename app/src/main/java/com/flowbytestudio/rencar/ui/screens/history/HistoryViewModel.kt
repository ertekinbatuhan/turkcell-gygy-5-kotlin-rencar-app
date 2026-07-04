package com.flowbytestudio.rencar.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.rentals.RentalRepository
import com.flowbytestudio.rencar.data.rentals.RentalWithVehicle
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val rentalRepository: RentalRepository = RentalRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadRentals()
    }

    private fun loadRentals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            rentalRepository.getMyRentals()
                .onSuccess { rentals ->
                    _uiState.value = HistoryUiState(
                        rentals = rentals
                            .filter { it.rental.status != "ACTIVE" }
                            .map { it.toUiModel() },
                        isLoading = false,
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message,
                    )
                }
        }
    }
}

private val displayDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale("tr"))
private val displayDateOnlyFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("tr"))

private fun formatDateTime(iso: String): String = runCatching {
    OffsetDateTime.parse(iso).format(displayDateFormatter)
}.getOrDefault(iso)

private fun formatDateOnly(iso: String): String = runCatching {
    OffsetDateTime.parse(iso).format(displayDateOnlyFormatter)
}.getOrDefault(iso)

// durationMinutes/distanceKm are mock-only: RentalResponseDto has no such fields.
// Kept as placeholder values until the backend exposes trip telemetry.
private fun RentalWithVehicle.toUiModel(): RentalUiModel = RentalUiModel(
    id = rental.id,
    vehicleId = rental.vehicleId,
    vehicleLabel = vehicle?.let { "${it.brand} ${it.model}" } ?: rental.vehicleId,
    startDate = formatDateTime(rental.startDate),
    endDate = formatDateOnly(rental.endDate),
    totalPrice = rental.totalPrice,
    status = when (rental.status) {
        "ACTIVE" -> RentalStatus.ACTIVE
        "CANCELLED" -> RentalStatus.CANCELLED
        else -> RentalStatus.COMPLETED
    },
    durationMinutes = 0,
    distanceKm = 0.0,
)

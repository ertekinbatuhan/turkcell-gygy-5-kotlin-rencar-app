package com.flowbytestudio.rencar.ui.screens.tripsummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.rentals.RentalRepository
import com.flowbytestudio.rencar.data.vehicles.VehicleRepository
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripSummaryViewModel(
    private val rentalId: String,
    private val rentalRepository: RentalRepository = RentalRepository(),
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripSummaryUiState())
    val uiState: StateFlow<TripSummaryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            rentalRepository.getRental(rentalId)
                .onSuccess { rental ->
                    val start = parseIsoMillis(rental.startDate)
                    val end = parseIsoMillis(rental.endDate)
                    val duration = if (start != null && end != null && end > start) {
                        (end - start) / 60_000
                    } else {
                        null
                    }
                    _uiState.update {
                        it.copy(isLoading = false, rental = rental, durationMinutes = duration)
                    }
                    vehicleRepository.getVehicle(rental.vehicleId).onSuccess { vehicle ->
                        _uiState.update { it.copy(vehicle = vehicle) }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, loadError = "Kiralama özeti yüklenemedi. Lütfen tekrar dene.")
                    }
                }
        }
    }
}

private fun parseIsoMillis(value: String): Long? {
    val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'")
    for (pattern in patterns) {
        val millis = runCatching {
            SimpleDateFormat(pattern, Locale.US)
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .parse(value)?.time
        }.getOrNull()
        if (millis != null) return millis
    }
    return null
}

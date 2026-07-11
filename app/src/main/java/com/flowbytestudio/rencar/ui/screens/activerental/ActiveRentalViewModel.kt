package com.flowbytestudio.rencar.ui.screens.activerental

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.rentals.RentalRepository
import com.flowbytestudio.rencar.data.vehicles.VehicleRepository
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ActiveRentalViewModel(
    private val rentalId: String,
    private val rentalRepository: RentalRepository = RentalRepository(),
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveRentalUiState())
    val uiState: StateFlow<ActiveRentalUiState> = _uiState.asStateFlow()

    private var rentalStartMillis: Long? = null

    init {
        load()
        startTicker()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            rentalRepository.getRental(rentalId)
                .onSuccess { rental ->
                    rentalStartMillis = parseIsoMillis(rental.startDate) ?: System.currentTimeMillis()
                    _uiState.update { it.copy(isLoading = false, rental = rental) }
                    vehicleRepository.getVehicle(rental.vehicleId).onSuccess { vehicle ->
                        _uiState.update { it.copy(vehicle = vehicle) }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, loadError = "Kiralama bilgileri yüklenemedi. Lütfen tekrar dene.")
                    }
                }
        }
    }

    private fun startTicker() {
        viewModelScope.launch {
            while (true) {
                rentalStartMillis?.let { start ->
                    val elapsed = ((System.currentTimeMillis() - start) / 1000).coerceAtLeast(0)
                    _uiState.update { it.copy(elapsedSeconds = elapsed) }
                }
                delay(1_000)
            }
        }
    }

    fun endRental() {
        if (_uiState.value.isEnding) return
        viewModelScope.launch {
            _uiState.update { it.copy(isEnding = true, endError = null) }
            rentalRepository.returnRental(rentalId)
                .onSuccess { rental ->
                    _uiState.update { it.copy(isEnding = false, endedTotalPrice = rental.totalPrice) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isEnding = false, endError = throwable.toEndErrorMessage())
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

private fun Throwable.toEndErrorMessage(): String = when {
    this is HttpException && code() == 409 -> "Bu kiralama zaten sonlandırılmış."
    this is HttpException && code() == 404 -> "Kiralama bulunamadı."
    this is HttpException && code() == 401 -> "Oturumun sona ermiş. Lütfen tekrar giriş yap."
    else -> "Kiralama sonlandırılamadı. Lütfen tekrar dene."
}

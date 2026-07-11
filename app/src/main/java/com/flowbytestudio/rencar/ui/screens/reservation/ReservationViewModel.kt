package com.flowbytestudio.rencar.ui.screens.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.rentals.RentalRepository
import com.flowbytestudio.rencar.data.vehicles.VehicleRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ReservationViewModel(
    private val vehicleId: String,
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val rentalRepository: RentalRepository = RentalRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    init {
        loadVehicle()
    }

    fun loadVehicle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingVehicle = true, loadError = null) }
            vehicleRepository.getVehicle(vehicleId)
                .onSuccess { vehicle ->
                    // Fiyatı API'den gelen ilk plan seçili başlar; dakikalık/saatlik
                    // fiyatlar backend'e eklenene kadar bu her zaman GUNLUK olur.
                    val defaultPlan = when {
                        vehicle.pricePerMinute != null -> RentalPlan.DAKIKALIK
                        vehicle.pricePerHour != null -> RentalPlan.SAATLIK
                        else -> RentalPlan.GUNLUK
                    }
                    _uiState.update {
                        it.copy(isLoadingVehicle = false, vehicle = vehicle, selectedPlan = defaultPlan)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoadingVehicle = false, loadError = throwable.toLoadErrorMessage())
                    }
                }
        }
    }

    fun onPlanSelect(plan: RentalPlan) {
        _uiState.update { it.copy(selectedPlan = plan) }
    }

    fun onTermsToggle(accepted: Boolean) {
        _uiState.update { it.copy(termsAccepted = accepted) }
    }

    fun submit(onCompleted: () -> Unit) {
        val state = _uiState.value
        if (!state.canSubmit) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            rentalRepository.createRental(vehicleId, isoEndDate(state.days))
                .onSuccess { rental ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            reservationCompleted = true,
                            completedTotalPrice = rental.totalPrice,
                        )
                    }
                    onCompleted()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isSubmitting = false, submitError = throwable.toSubmitErrorMessage())
                    }
                }
        }
    }
}

private fun isoEndDate(days: Int): String {
    val millis = System.currentTimeMillis() + days * 24L * 60 * 60 * 1000
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return format.format(Date(millis))
}

private fun Throwable.toLoadErrorMessage(): String = when {
    this is HttpException && code() == 404 -> "Bu araç artık müsait değil."
    this is HttpException && code() == 401 -> "Oturumun sona ermiş. Lütfen tekrar giriş yap."
    else -> "Araç bilgileri yüklenemedi. Lütfen tekrar dene."
}

private fun Throwable.toSubmitErrorMessage(): String = when {
    this is HttpException && code() == 409 -> "Bu araç artık müsait değil ya da zaten aktif bir kiralaman var."
    this is HttpException && code() == 404 -> "Araç bulunamadı."
    this is HttpException && code() == 401 -> "Oturumun sona ermiş. Lütfen tekrar giriş yap."
    else -> "Rezervasyon oluşturulamadı. Lütfen tekrar dene."
}

package com.flowbytestudio.rencar.ui.screens.handover

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

class HandoverViewModel(
    private val vehicleId: String,
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
    private val rentalRepository: RentalRepository = RentalRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(HandoverUiState())
    val uiState: StateFlow<HandoverUiState> = _uiState.asStateFlow()

    init {
        loadVehicle()
    }

    fun loadVehicle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingVehicle = true, loadError = null) }
            vehicleRepository.getVehicle(vehicleId)
                .onSuccess { vehicle ->
                    _uiState.update { it.copy(isLoadingVehicle = false, vehicle = vehicle) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoadingVehicle = false, loadError = "Araç bilgileri yüklenemedi. Lütfen tekrar dene.")
                    }
                }
        }
    }

    // Backend'de henüz foto yükleme endpoint'i yok; şimdilik çekim simüle ediliyor.
    // Endpoint gelince burada kameradan alınan görüntü upload edilip URL saklanacak.
    fun onCapture(side: PhotoSide) {
        _uiState.update { it.copy(capturedSides = it.capturedSides + side) }
    }

    fun onRetake(side: PhotoSide) {
        _uiState.update { it.copy(capturedSides = it.capturedSides - side) }
    }

    fun startRental() {
        val state = _uiState.value
        if (!state.canStart) return
        viewModelScope.launch {
            _uiState.update { it.copy(isStarting = true, startError = null) }
            rentalRepository.createRental(vehicleId, isoEndDate())
                .onSuccess { rental ->
                    _uiState.update { it.copy(isStarting = false, startedRentalId = rental.id) }
                }
                .onFailure { throwable ->
                    val message = resolveStartError(throwable)
                    _uiState.update { it.copy(isStarting = false, startError = message) }
                }
        }
    }

    // 409 iki farklı durumda dönüyor: araç dolu ya da kullanıcının zaten aktif kiralaması
    // var. Kiralamaları kontrol edip kullanıcıya doğru mesajı gösteriyoruz.
    private suspend fun resolveStartError(throwable: Throwable): String {
        if (throwable is HttpException && throwable.code() == 409) {
            val hasActiveRental = rentalRepository.getMyRentals()
                .getOrDefault(emptyList())
                .any { it.rental.status == "ACTIVE" }
            return if (hasActiveRental) {
                "Zaten aktif bir kiralaman var. Yeni kiralama için önce mevcut kiralamayı bitirmelisin."
            } else {
                "Bu araç artık müsait değil."
            }
        }
        return throwable.toStartErrorMessage()
    }
}

private fun isoEndDate(): String {
    val millis = System.currentTimeMillis() + 24L * 60 * 60 * 1000
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return format.format(Date(millis))
}

private fun Throwable.toStartErrorMessage(): String = when {
    this is HttpException && code() == 409 -> "Bu araç artık müsait değil ya da zaten aktif bir kiralaman var."
    this is HttpException && code() == 404 -> "Araç bulunamadı."
    this is HttpException && code() == 401 -> "Oturumun sona ermiş. Lütfen tekrar giriş yap."
    else -> "Kiralama başlatılamadı. Lütfen tekrar dene."
}

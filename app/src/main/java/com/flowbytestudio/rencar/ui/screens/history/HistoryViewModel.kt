package com.flowbytestudio.rencar.ui.screens.history

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.R
import com.flowbytestudio.rencar.data.rentals.RentalDto
import com.flowbytestudio.rencar.data.rentals.RentalRepository
import com.flowbytestudio.rencar.ui.common.formatTl
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val rentalRepository: RentalRepository = RentalRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadRentals()
        loadStats()
    }

    // Kullanıcı bu ekrana her döndüğünde (ör. bir yolculuk ödendikten sonra)
    // listeyi ve aylık özeti backend'den tazeler.
    fun refresh() {
        loadRentals()
        loadStats()
    }

    private fun loadRentals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            rentalRepository.getMyRentals()
                .onSuccess { rentals ->
                    _uiState.update {
                        it.copy(
                            rentals = rentals.map { rental -> rental.toUiModel() },
                            isLoading = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = R.string.history_load_error,
                        )
                    }
                }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            rentalRepository.getStats()
                .onSuccess { stats ->
                    _uiState.update {
                        it.copy(
                            tripCountThisMonth = stats.tripCount,
                            totalSpentThisMonth = stats.totalSpent,
                            statsErrorMessage = null,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(statsErrorMessage = R.string.history_stats_load_error)
                    }
                }
        }
    }
}

private val displayDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale("tr"))

private fun formatDateTime(iso: String): String = runCatching {
    OffsetDateTime.parse(iso).format(displayDateFormatter)
}.getOrDefault(iso)

@StringRes
private fun planLabelRes(plan: String): Int? = when (plan) {
    "PER_MINUTE" -> R.string.common_plan_per_minute
    "HOURLY" -> R.string.common_plan_hourly
    "DAILY" -> R.string.common_plan_daily
    else -> null
}

private fun RentalDto.toUiModel(): RentalUiModel {
    val statusEnum = when (status) {
        "PREPARING" -> RentalStatus.PREPARING
        "ACTIVE" -> RentalStatus.ACTIVE
        "COMPLETED" -> RentalStatus.COMPLETED
        "CANCELLED" -> RentalStatus.CANCELLED
        else -> RentalStatus.OTHER
    }
    return RentalUiModel(
        id = id,
        vehicleId = vehicleId,
        vehicleLabel = vehicle?.let { "${it.brand} ${it.model} · ${it.plate}" } ?: vehicleId,
        planLabelRes = planLabelRes(plan),
        planLabelRaw = plan,
        dateLabel = startedAt?.let { formatDateTime(it) } ?: "—",
        priceLabel = totalPrice?.let { "₺${formatTl(it)}" } ?: "—",
        durationMinutes = durationMinutes,
        distanceKm = distanceKm,
        status = statusEnum,
        statusLabelRes = statusEnum.labelRes,
        statusLabelRaw = status,
        isUnpaidCompleted = status == "COMPLETED" && paymentStatus == "UNPAID",
    )
}

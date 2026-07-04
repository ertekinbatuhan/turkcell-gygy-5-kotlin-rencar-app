package com.flowbytestudio.rencar.ui.screens.wallet

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

// Balance and saved cards are mock-only: the API has no wallet/balance/payment-method
// endpoints. Only the transaction list is backed by real data (rentals from GET /rentals).
class WalletViewModel(
    private val rentalRepository: RentalRepository = RentalRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState(isLoading = true))
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            rentalRepository.getMyRentals()
                .onSuccess { rentals ->
                    _uiState.value = WalletUiState(
                        transactions = rentals
                            .sortedByDescending { it.rental.createdAt }
                            .map { it.toTransactionUiModel() },
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

private val displayDateFormatter = DateTimeFormatter.ofPattern("d MMM · HH:mm", Locale("tr"))

private fun formatDateTime(iso: String): String = runCatching {
    OffsetDateTime.parse(iso).format(displayDateFormatter)
}.getOrDefault(iso)

private fun RentalWithVehicle.toTransactionUiModel(): WalletTransactionUiModel = WalletTransactionUiModel(
    id = rental.id,
    title = vehicle?.let { "${it.brand} ${it.model} kiralama" } ?: "Araç kiralama",
    date = formatDateTime(rental.createdAt),
    amount = -rental.totalPrice,
)

package com.flowbytestudio.rencar.ui.screens.history

data class HistoryUiState(
    val rentals: List<RentalUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val tripCountThisMonth: Int get() = rentals.size
    val totalSpentThisMonth: Double get() = rentals.sumOf { it.totalPrice }
}

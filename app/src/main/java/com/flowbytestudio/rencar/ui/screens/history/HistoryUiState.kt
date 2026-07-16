package com.flowbytestudio.rencar.ui.screens.history

data class HistoryUiState(
    val rentals: List<RentalUiModel> = emptyList(),
    // GET /rentals/stats'ten gelir (bu ayın tamamlanmış yolculukları).
    val tripCountThisMonth: Int = 0,
    val totalSpentThisMonth: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val statsErrorMessage: String? = null,
)

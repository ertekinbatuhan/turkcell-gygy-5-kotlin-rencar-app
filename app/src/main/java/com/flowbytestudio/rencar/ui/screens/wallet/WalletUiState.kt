package com.flowbytestudio.rencar.ui.screens.wallet

data class WalletTransactionUiModel(
    val id: String,
    val title: String,
    val date: String,
    val amount: Double,
)

data class WalletUiState(
    val transactions: List<WalletTransactionUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

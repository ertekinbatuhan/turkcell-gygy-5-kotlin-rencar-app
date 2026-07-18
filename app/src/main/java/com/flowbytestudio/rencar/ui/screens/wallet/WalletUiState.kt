package com.flowbytestudio.rencar.ui.screens.wallet

// Cüzdan hareketinin görsel türü (ikon + renk seçimi ekranda buna göre yapılır).
enum class WalletTransactionKind { TOPUP, RENTAL_PAYMENT, REFERRAL_BONUS, OTHER }

data class WalletTransactionUiModel(
    val id: String,
    val kind: WalletTransactionKind,
    val title: String,
    val subtitle: String,
    // İşaretli tutar (TL): + yükleme/bonus, - ödeme.
    val amount: Double,
)

data class WalletCardUiModel(
    val id: String,
    // VISA / MASTERCARD
    val brand: String,
    val last4: String,
    // "AA/YY" biçiminde son kullanma tarihi.
    val expiry: String,
    val isDefault: Boolean,
)

data class WalletUiState(
    // İlk yükleme durumu (cüzdan + kartlar).
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val balance: Double = 0.0,
    val transactions: List<WalletTransactionUiModel> = emptyList(),
    val cards: List<WalletCardUiModel> = emptyList(),

    // Bakiye yükleme sayfası (bottom sheet).
    val showTopupSheet: Boolean = false,
    val isToppingUp: Boolean = false,
    val topupError: String? = null,

    // Kart ekleme sayfası (bottom sheet).
    val showAddCardSheet: Boolean = false,
    val isAddingCard: Boolean = false,
    val addCardError: String? = null,

    // Kart aksiyonları (öntanımlı yap / sil).
    val cardActionInProgress: Boolean = false,
    val cardActionError: String? = null,
)

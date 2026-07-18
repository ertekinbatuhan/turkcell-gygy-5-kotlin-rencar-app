package com.flowbytestudio.rencar.data.wallet

import kotlinx.serialization.Serializable

@Serializable
data class WalletTransactionDto(
    val id: String,
    // TOPUP / RENTAL_PAYMENT / REFERRAL_BONUS
    val type: String,
    // İşaretli tutar (TL): yükleme/bonus +, ödeme -.
    val amount: Double,
    val rentalId: String? = null,
    val description: String,
    val createdAt: String,
)

// Bakiye + son 20 işlem (yeniden eskiye).
@Serializable
data class WalletResponse(
    val id: String,
    val balance: Double,
    val transactions: List<WalletTransactionDto> = emptyList(),
)

@Serializable
data class TopupRequest(
    // 10-5000 TL aralığı; simülasyon, gerçek tahsilat yapılmaz.
    val amount: Double,
)
